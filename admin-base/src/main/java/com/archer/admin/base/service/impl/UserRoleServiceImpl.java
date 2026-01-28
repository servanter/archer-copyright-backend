package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.UserRole;
import com.archer.admin.base.repository.UserRoleMapper;
import com.archer.admin.base.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
