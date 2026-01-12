package com.archer.admin.web.login.service;

import com.archer.admin.base.entities.Menu;
import com.archer.admin.base.entities.Role;
import com.archer.admin.base.entities.RoleMenu;
import com.archer.admin.base.entities.User;
import com.archer.admin.base.entities.UserRole;
import com.archer.admin.base.service.MenuService;
import com.archer.admin.base.service.RoleMenuService;
import com.archer.admin.base.service.RoleService;
import com.archer.admin.base.service.UserRoleService;
import com.archer.admin.base.service.UserService;
import com.archer.admin.web.login.entities.LoginTransform.LoginReq;
import com.archer.admin.web.login.entities.LoginTransform.LoginRes;
import com.archer.admin.web.login.entities.LoginTransform.MenuData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizLoginService {

    @Resource
    private UserService userService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleMenuService roleMenuService;

    @Resource
    private MenuService menuService;

    public LoginRes login(LoginReq loginReq) {
        User user = userService.lambdaQuery()
                .eq(User::getUserName, loginReq.getUsername())
                .eq(User::getPassword, loginReq.getPassword())
                .eq(User::getValid, 1)
                .one();

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = generateToken();
        List<MenuData> menuDataList = buildMenuData(user.getId());

        return LoginRes.builder()
                .token(token)
                .menuData(menuDataList)
                .build();
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private List<MenuData> buildMenuData(Integer userId) {
        List<UserRole> userRoles = userRoleService.lambdaQuery()
                .eq(UserRole::getUserId, userId)
                .list();

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        List<RoleMenu> roleMenus = roleMenuService.lambdaQuery()
                .in(RoleMenu::getRoleId, roleIds)
                .list();

        if (CollectionUtils.isEmpty(roleMenus)) {
            return Collections.emptyList();
        }

        List<Integer> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        List<Menu> menus = menuService.lambdaQuery()
                .in(Menu::getId, menuIds)
                .eq(Menu::getValid, 1)
                .list();

        return buildMenuTree(menus);
    }

    private List<MenuData> buildMenuTree(List<Menu> menus) {
        List<MenuData> result = new ArrayList<>();

        // 1. 查询顶级菜单（parentId = -1）
        for (Menu menu : menus) {
            if (menu.getParentId() == -1) {


                // 2. 根据顶级菜单ID查询下属菜单
                List<MenuData> children = Lists.newArrayListWithCapacity(1);
                for (Menu childMenu : menus) {
                    if (childMenu.getParentId().equals(menu.getId())) {
                        MenuData childData = MenuData.builder()
                                .path("/" + menu.getUrl().split("/")[1].toLowerCase())
                                .url(menu.getUrl())
                                .name(menu.getName().toLowerCase())
                                .label(menu.getName())
                                .icon("setting")
                                .build();

                        children.add(childData);
                    }
                }

                MenuData menuData = MenuData.builder()
                        .path("/" + menu.getUrl().split("/")[1].toLowerCase())
                        .url(menu.getUrl())
                        .name(menu.getName().toLowerCase())
                        .label(menu.getName())
                        .icon("user")
                        .children(children)
                        .build();

                result.add(menuData);
            }
        }

        return result;
    }
}
