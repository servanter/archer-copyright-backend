package com.archer.admin.web.menu.service;

import com.archer.admin.base.entities.Menu;
import com.archer.admin.base.service.MenuService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.menu.entities.MenuTransform.MenuQueryReq;
import com.archer.admin.web.menu.entities.MenuTransform.MenuQueryRes;
import com.archer.admin.web.menu.entities.MenuTransform.MenuRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizMenuService {

    @Resource
    private MenuService menuService;

    public MenuRes query(int menuId) {
        return MenuRes.parseRes(menuService.getById(menuId));
    }

    public Result modify(Menu menu) {
        return menuService.updateById(menu) ? Result.success() : Result.error();
    }

    public MenuQueryRes list(MenuQueryReq menuQueryReq) {
        Page<Menu> page = menuService.lambdaQuery()
                
.like(StringUtils.isNotBlank(menuQueryReq.getName()), Menu::getName, menuQueryReq.getName())
.eq(menuQueryReq.getParentId() != 0, Menu::getParentId, menuQueryReq.getParentId())
.like(StringUtils.isNotBlank(menuQueryReq.getUrl()), Menu::getUrl, menuQueryReq.getUrl())
.eq(menuQueryReq.getOperatorId() != 0, Menu::getOperatorId, menuQueryReq.getOperatorId())
.eq(menuQueryReq.getValid() != 0, Menu::getValid, menuQueryReq.getValid())

                .page(new Page<>(menuQueryReq.getPageNo(), menuQueryReq.getPageSize()));

        List<MenuRes> list = page.getRecords().stream().map(MenuRes::parseRes).collect(Collectors.toList());
        return MenuQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(int menuId) {
        Menu menu = new Menu();
        menu.setId(menuId);
        menu.setValid(-1);
        return menuService.updateById(menu) ? Result.success() : Result.error();
    }

    public Result save(Menu menu) {
        return menuService.save(menu) ? Result.success() : Result.error();
    }
}
