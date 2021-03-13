package org.geektimes.project.user.web.controller;


import org.geektimes.project.user.domian.User;
import org.geektimes.project.user.service.UserServiceImpl;
import org.geektimes.project.user.validator.DelegatingValidator;
import org.geektimes.web.mvc.controller.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.io.IOException;

/**
 * @author djt
 * @date 2021/3/4
 */
@Resource(name="bean/UserController")
public class UserController implements RestController {


    @Resource(name="bean/UserService")
    private UserServiceImpl userService;



    @Path("/all")
    public void getAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = userService.queryUserById(1L);
        response.getWriter().print(user);
    }
}
