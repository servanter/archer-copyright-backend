package com.archer.admin.web.user.controller;

import com.archer.admin.base.entities.User;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.component.WebContext;
import com.archer.admin.web.user.entities.UserTransform.UserQueryReq;
import com.archer.admin.web.user.entities.UserTransform.UserQueryRes;
import com.archer.admin.web.user.entities.UserTransform.UserRes;
import com.archer.admin.web.user.service.BizUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/user")
@RestController
@ResponseResultBody
public class UserController {

    @Resource
    private BizUserService bizUserService;

    @RequestMapping("/detail/{userId}")
    public UserRes detail(WebContext webContext, @PathVariable("userId") int userId) {
        return bizUserService.query(userId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody User user) {
        return bizUserService.modify(user);
    }

    @RequestMapping("/list")
    public UserQueryRes list(WebContext webContext, UserQueryReq userQueryReq) {
        return bizUserService.list(userQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, int id) {
        return bizUserService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody User user) {
        return bizUserService.save(user);
    }
}
