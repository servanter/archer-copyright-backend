package com.archer.admin.web.login.service;

import com.archer.admin.base.entities.Menu;
import com.archer.admin.base.entities.User;
import com.archer.admin.base.repository.MenuMapper;
import com.archer.admin.base.service.UserService;
import com.archer.admin.web.common.ValidEnum;
import com.archer.admin.web.exception.BizException;
import com.archer.admin.web.login.entities.LoginTransform.LoginMenuRes;
import com.archer.admin.web.login.entities.LoginTransform.LoginReq;
import com.archer.admin.web.login.entities.LoginTransform.LoginRes;
import com.archer.admin.web.util.TokenUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LoginService {

    @Resource
    private UserService userService;
    @Resource
    private MenuMapper menuMapper;

    public LoginRes login(LoginReq loginReq) throws BizException {
        Optional<User> opt = userService.lambdaQuery()
                .eq(User::getUserName, loginReq.getUserName())
                .eq(User::getPassword, loginReq.getPassword())
                .oneOpt();

        return opt.map(this::login0)
                .orElseThrow(() -> new BizException(-90001));
    }

    private LoginRes login0(User user) {
        String userName = user.getUserName();
        Integer userId = user.getId();

        // 当前用户
        List<Menu> menus = menuMapper.selectByUserId(userId).stream().distinct().collect(Collectors.toList());

        // 所有
        List<Menu> allMenus = menuMapper.selectList(new QueryWrapper<Menu>().eq("valid", ValidEnum.ACTIVE.getValue()));

        return LoginRes.builder()
                .token(TokenUtils.sign(userName, userId))
                .menus(getCurrentMenus(menus, allMenus))
                .build();
    }

    private List<LoginMenuRes> getCurrentMenus(List<Menu> menus, List<Menu> allMenus) {

        Map<Integer, List<Menu>> groupMenus = allMenus.stream()
                .filter(x -> x.getParentId() > 0)
                .collect(Collectors.groupingBy(Menu::getParentId));

        return menus.stream()
                .map(menu -> getCurrentMenus0(menu, groupMenus))
                .collect(Collectors.toList());
    }

    private LoginMenuRes getCurrentMenus0(Menu menu, Map<Integer, List<Menu>> groupMenus) {
        List<LoginMenuRes> subMenus = groupMenus.getOrDefault(menu.getId(), Collections.emptyList()).stream()
                .map(m -> getCurrentMenus0(m, Collections.emptyMap()))
                .collect(Collectors.toList());
        return LoginMenuRes.builder()
                .name(menu.getUrl().contains("/") ? menu.getUrl().substring(menu.getUrl().lastIndexOf("/") + 1).toLowerCase() : menu.getUrl())
                .path(menu.getUrl().contains("/") ? menu.getUrl().substring(menu.getUrl().lastIndexOf("/")).toLowerCase() : menu.getUrl())
                .label(menu.getName())
                .icon("user")
                .url(menu.getUrl())
                .children(subMenus)
                .build();
    }
}
