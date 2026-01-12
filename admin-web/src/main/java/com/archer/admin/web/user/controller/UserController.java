package com.archer.admin.web.user.controller;

import com.archer.admin.base.entities.User;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.user.entities.UserTransform.UserQueryReq;
import com.archer.admin.web.user.entities.UserTransform.UserQueryRes;
import com.archer.admin.web.user.entities.UserTransform.UserRes;
import com.archer.admin.web.user.service.BizUserService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.archer.admin.web.component.ResponseResultBody;

@RequestMapping("/user")
@RestController
@ResponseResultBody
public class UserController {

    @Resource
    private BizUserService bizUserService;

    @RequestMapping("/detail/{userId}")
    public UserRes detail(@PathVariable("userId") int userId) {
        return bizUserService.query(userId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(@RequestBody User user) {
        return bizUserService.modify(user);
    }

    @RequestMapping("/list")
    public UserQueryRes list(UserQueryReq userQueryReq) {
        return bizUserService.list(userQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(int id) {
        return bizUserService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody User user) {
        return bizUserService.save(user);
    }
}
