package com.archer.admin.web.role.controller;

import com.archer.admin.base.entities.Role;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.role.entities.RoleTransform.RoleQueryReq;
import com.archer.admin.web.role.entities.RoleTransform.RoleQueryRes;
import com.archer.admin.web.role.entities.RoleTransform.RoleRes;
import com.archer.admin.web.role.service.BizRoleService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.archer.admin.web.component.ResponseResultBody;

@RequestMapping("/role")
@RestController
@ResponseResultBody
public class RoleController {

    @Resource
    private BizRoleService bizRoleService;

    @RequestMapping("/detail/{roleId}")
    public RoleRes detail(@PathVariable("roleId") int roleId) {
        return bizRoleService.query(roleId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(@RequestBody Role role) {
        return bizRoleService.modify(role);
    }

    @RequestMapping("/list")
    public RoleQueryRes list(RoleQueryReq roleQueryReq) {
        return bizRoleService.list(roleQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(int id) {
        return bizRoleService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody Role role) {
        return bizRoleService.save(role);
    }
}
