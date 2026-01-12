package com.archer.admin.web.menu.controller;

import com.archer.admin.base.entities.Menu;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.menu.entities.MenuTransform.MenuQueryReq;
import com.archer.admin.web.menu.entities.MenuTransform.MenuQueryRes;
import com.archer.admin.web.menu.entities.MenuTransform.MenuRes;
import com.archer.admin.web.menu.service.BizMenuService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.archer.admin.web.component.ResponseResultBody;

@RequestMapping("/menu")
@RestController
@ResponseResultBody
public class MenuController {

    @Resource
    private BizMenuService bizMenuService;

    @RequestMapping("/detail/{menuId}")
    public MenuRes detail(@PathVariable("menuId") int menuId) {
        return bizMenuService.query(menuId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(@RequestBody Menu menu) {
        return bizMenuService.modify(menu);
    }

    @RequestMapping("/list")
    public MenuQueryRes list(MenuQueryReq menuQueryReq) {
        return bizMenuService.list(menuQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(int id) {
        return bizMenuService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody Menu menu) {
        return bizMenuService.save(menu);
    }
}
