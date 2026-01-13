package com.archer.admin.web.menu.controller;

import com.archer.admin.base.entities.Menu;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.component.WebContext;
import com.archer.admin.web.menu.entities.MenuTransform.*;
import com.archer.admin.web.menu.service.BizMenuService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/menu")
@RestController
@ResponseResultBody
public class MenuController {

    @Resource
    private BizMenuService bizMenuService;

    @RequestMapping("/detail/{menuId}")
    public MenuRes detail(WebContext webContext, @PathVariable("menuId") int menuId) {
        return bizMenuService.query(menuId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody Menu menu) {
        return bizMenuService.modify(menu);
    }

    @RequestMapping("/list")
    public MenuQueryRes list(WebContext webContext, MenuQueryReq menuQueryReq) {
        return bizMenuService.list(menuQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, int id) {
        return bizMenuService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody Menu menu) {
        return bizMenuService.save(menu);
    }

    @RequestMapping("/role")
    public MenuRoleRes list(WebContext webContext, @RequestParam("roleId") int roleId) {
        return bizMenuService.queryRoleMenus(roleId);
    }

    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody RoleMenuSetting roleMenuSetting) {
        roleMenuSetting.setOperatorId(webContext.getUserId());
        return bizMenuService.setting(roleMenuSetting);
    }

}
