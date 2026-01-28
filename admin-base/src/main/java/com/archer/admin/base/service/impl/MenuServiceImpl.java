package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.Menu;
import com.archer.admin.base.repository.MenuMapper;
import com.archer.admin.base.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Resource
    private MenuMapper menuMapper;
    @Override
    public List<Menu> queryRoleMenus(int roleId) {
        return menuMapper.getRoleMenus(roleId);
    }
}
