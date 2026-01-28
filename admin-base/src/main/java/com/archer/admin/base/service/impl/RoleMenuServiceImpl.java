package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.RoleMenu;
import com.archer.admin.base.repository.RoleMenuMapper;
import com.archer.admin.base.service.RoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
}
