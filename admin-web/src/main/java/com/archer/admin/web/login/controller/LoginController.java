package com.archer.admin.web.login.controller;

import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.login.entities.LoginTransform.LoginReq;
import com.archer.admin.web.login.entities.LoginTransform.LoginRes;
import com.archer.admin.web.login.service.BizLoginService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@ResponseResultBody
public class LoginController {

    @Resource
    private BizLoginService bizLoginService;

    @PostMapping
    public LoginRes login(@RequestBody LoginReq loginReq) {
        return bizLoginService.login(loginReq);
    }
}
