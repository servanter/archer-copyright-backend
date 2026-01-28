package com.archer.admin.web.login.controller;

import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.login.entities.LoginTransform.LoginReq;
import com.archer.admin.web.login.entities.LoginTransform.LoginRes;
import com.archer.admin.web.login.service.LoginService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/login")
@ResponseResultBody
public class LoginController {

    @Resource
    private LoginService loginService;

    @RequestMapping("/login")
    public LoginRes login(@RequestBody LoginReq loginReq) {
        return loginService.login(loginReq);
    }
}
