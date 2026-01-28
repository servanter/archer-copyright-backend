package com.archer.admin.web.role.controller;

import com.archer.admin.base.entities.Role;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.component.WebContext;
import com.archer.admin.web.role.entities.RoleTransform.*;
import com.archer.admin.web.role.service.BizRoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/role")
@RestController
@ResponseResultBody
public class RoleController {

    @Resource
    private BizRoleService bizRoleService;

    @RequestMapping("/detail/{roleId}")
    public RoleRes detail(WebContext webContext, @PathVariable("roleId") int roleId) {
        return bizRoleService.query(roleId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody Role role) {
        return bizRoleService.modify(role);
    }

    @RequestMapping("/list")
    public RoleQueryRes list(WebContext webContext, RoleQueryReq roleQueryReq) {
        return bizRoleService.list(roleQueryReq);
    }

    @RequestMapping("/user")
    public RoleUserRes queryUserRoles(WebContext webContext, @RequestParam("userId") int userId) {
        return bizRoleService.queryUserRoles(userId);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, int id) {
        return bizRoleService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody Role role) {
        return bizRoleService.save(role);
    }

    @RequestMapping("/setting")
    public Result setting(WebContext webContext, @RequestBody UserRoleSetting userRoleSetting) {
        userRoleSetting.setOperatorId(webContext.getUserId());
        return bizRoleService.settingRoles(userRoleSetting);
    }
}
