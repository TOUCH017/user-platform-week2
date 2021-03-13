package org.geektimes.project.user.web.controller;


import org.geektimes.project.user.domian.User;
import org.geektimes.project.user.service.UserServiceImpl;
import org.geektimes.project.user.validator.DelegatingValidator;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Set;

/**
 * @author djt
 * @date 2021/3/3
 */
@Resource(name="bean/RegisterController")
public class RegisterController implements PageController {


    @Resource(name="bean/UserService")
    private UserServiceImpl userService;
    @Resource(name="bean/Validator")
    private DelegatingValidator delegatingValidator;




    @GET
    @Path("/register")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = new User();
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setPassword(password);
        Set<ConstraintViolation<User>> validate = delegatingValidator.validate(user);
        if (validate.size()>0){
            String  msg="";
            for (ConstraintViolation<User> userConstraintViolation : validate) {
                msg+=userConstraintViolation.getMessage()+";";
            }
            request.setAttribute("failureMsg",msg.substring(0,msg.length()-1));
            return "failure.jsp";
        }
        User register = userService.register(user);
        User loginUser = userService.queryUserById(register.getId());
        request.setAttribute("name",loginUser.getName());
        return "success.jsp" ;
    }

}
