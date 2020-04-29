package com.neu.crm.controller;

import com.neu.crm.bean.User;
import com.neu.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    public String verify(User user, HttpServletRequest request, ModelMap modelMap){
        User loginUser = userService.login(user);
        if (loginUser!=null){
            loginUser.setPassword("");
            //登录成功，写入session
            request.getSession().setAttribute("user",loginUser);
            return "redirect:/index";
        }
        modelMap.put("errorMsg","用户名或密码输入错误，请重试");
        return "login";
    }

    @GetMapping("register")
    public String register(){
        return "register";
    }

    @PostMapping("register")
    @ResponseBody
    public Boolean registerNewAccount(User user){
        return userService.register(user);
    }

    @GetMapping("logout")
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return "redirect:/login";
    }

}
