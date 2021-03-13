package org.geektimes.web.mvc.base;


import org.apache.commons.lang.StringUtils;
import org.geektimes.web.mvc.context.ComponentContext;
import org.geektimes.web.mvc.controller.Controller;
import org.geektimes.web.mvc.controller.PageController;
import org.geektimes.web.mvc.controller.RestController;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.substringAfter;

/**
 * @author djt
 * @date 2021/3/3
 */
public class FrontControllerServlet extends HttpServlet {

    private Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>();

    private Map<String, Controller> controllersMapping = new HashMap<>();


    @Override
    public void init(ServletConfig servletConfig){
        ServletContext servletContext = servletConfig.getServletContext();
        initHandleMethods(servletContext);
    }


    private void initHandleMethods( ServletContext servletContext){
        ServiceLoader<Controller> load = ServiceLoader.load(Controller.class);
        for (Controller controller : load) {
            Class<? extends Controller> controllerClass = controller.getClass();
            Path pathAnnotation = controllerClass.getAnnotation(Path.class);
            Resource resourceAnnotation = controllerClass.getAnnotation(Resource.class);
            if (resourceAnnotation == null ||  resourceAnnotation.name() == null){
                return;
            }
            ComponentContext componentContext =(ComponentContext) servletContext.getAttribute(ComponentContext.class.getName());
            Controller controllerComponent = (Controller)componentContext.lookupComponent(resourceAnnotation.name());
            String prefixRequestPath="";

            if (pathAnnotation != null){
                prefixRequestPath += pathAnnotation.value();
            }

            Method[] publicMethods = controllerClass.getMethods();
            for (Method method : publicMethods) {
                Set<String> supportedHttpMethods = findSupportedHttpMethods(method);
                Path methodPathAnnotation = method.getAnnotation(Path.class);
                if (methodPathAnnotation != null){
                   String methodRequestPath=prefixRequestPath+methodPathAnnotation.value();
                    handleMethodInfoMapping.put(methodRequestPath,
                            new HandlerMethodInfo(methodRequestPath, method, supportedHttpMethods));
                    controllersMapping.put(methodRequestPath, controllerComponent);
                }
            }
        }
    }


    private Set<String> findSupportedHttpMethods(Method method){
        Set<String> supportedHttpMethods = new HashSet<>();
        Annotation[] methodAnnotations = method.getAnnotations();
        for (Annotation annotation : methodAnnotations) {
            HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null){
                supportedHttpMethods.add(httpMethod.value());
            }
        }
        if (supportedHttpMethods.isEmpty()) {
            supportedHttpMethods.addAll(asList(HttpMethod.GET, HttpMethod.POST,
                    HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
        }
        return supportedHttpMethods;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String servletContextPath = request.getContextPath();
        String prefixPath = servletContextPath;
        String requestMappingPath = substringAfter(requestURI,
                StringUtils.replace(prefixPath, "//", "/"));
        Controller controller = controllersMapping.get(requestMappingPath);

        if (controller != null){

            HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);

            try{
                if (handlerMethodInfo != null){

                    Set<String> supportedHttpMethods = handlerMethodInfo.getSupportedHttpMethods();
                    String httpMethod = request.getMethod();
                    Method handlerMethod = handlerMethodInfo.getHandlerMethod();

                    if (!supportedHttpMethods.contains(httpMethod)){
                        // HTTP 方法不支持
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }

                    if (controller instanceof PageController) {

                        PageController pageController = PageController.class.cast(controller);
                        String viewPath = pageController.execute(request, response);
                        ServletContext servletContext = request.getServletContext();

                        if (!viewPath.startsWith("/")) {
                            viewPath = "/" + viewPath;
                        }

                        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(viewPath);
                        requestDispatcher.forward(request, response);

                        return;
                    }else if (controller instanceof RestController){
                        handlerMethod.invoke(controller,request,response);
                    }
                }
            } catch (Throwable throwable) {
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException) throwable.getCause();
                } else {
                    throw new ServletException(throwable.getCause());
                }
            }

        }
    }

}
