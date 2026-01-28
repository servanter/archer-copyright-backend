package com.archer.admin.web.menu.service;

import com.archer.admin.base.entities.Menu;
import com.archer.admin.base.entities.RoleMenu;
import com.archer.admin.base.service.MenuService;
import com.archer.admin.base.service.RoleMenuService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.menu.entities.MenuTransform.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BizMenuService {

    @Resource
    private MenuService menuService;
    @Resource
    private RoleMenuService roleMenuService;

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
                .eq(Menu::getValid, 1)

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

    public MenuRoleRes queryRoleMenus(int roleId) {
        // 当前
        List<Menu> dbCurMenus = menuService.queryRoleMenus(roleId);

        // 全部
        List<Menu> dbAllMenus = menuService.lambdaQuery()
                .eq(Menu::getValid, 1)
                .list();

        // 组装
        List<MenuRes> curMenus = assemblyMenus(dbCurMenus);
        List<MenuRes> allMenus = assemblyMenus(dbAllMenus);

        return MenuRoleRes.builder()
                .curMenus(curMenus)
                .allMenus(allMenus)
                .build();
    }

    private List<MenuRes> assemblyMenus(List<Menu> allList) {
        List<Menu> topMenus = allList.stream()
                .filter(m -> m.getParentId() <= 0)
                .collect(Collectors.toList());

        Map<Integer, List<Menu>> subMenus = allList.stream()
                .filter(m -> m.getParentId() > 0)
                .collect(Collectors.groupingBy(Menu::getParentId));

        return topMenus.stream().map(m -> toRes(m, subMenus)).collect(Collectors.toList());
    }

    private MenuRes toRes(Menu m, Map<Integer, List<Menu>> subMenus) {
        return MenuRes.parseRes(m, subMenus.getOrDefault(m.getId(), Collections.emptyList()));
    }

    public Result setting(RoleMenuSetting roleMenuSetting) {

        roleMenuService.lambdaUpdate()
                .eq(RoleMenu::getRoleId, roleMenuSetting.getRoleId())
                .remove();

        if (CollectionUtils.isNotEmpty(roleMenuSetting.getMenuIds())) {
            List<RoleMenu> userRoles = roleMenuSetting.getMenuIds().stream()
                    .map(menuId -> toRoleMenu(roleMenuSetting, menuId))
                    .collect(Collectors.toList());
            roleMenuService.saveBatch(userRoles);
        }

        return Result.success();
    }

    private RoleMenu toRoleMenu(RoleMenuSetting roleMenuSetting, Integer menuId) {
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setRoleId(roleMenuSetting.getRoleId());
        roleMenu.setMenuId(menuId);
        roleMenu.setOperatorId(roleMenuSetting.getOperatorId());
        return roleMenu;
    }
}
