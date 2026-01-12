package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.Role;
import com.archer.admin.base.repository.RoleMapper;
import com.archer.admin.base.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}
