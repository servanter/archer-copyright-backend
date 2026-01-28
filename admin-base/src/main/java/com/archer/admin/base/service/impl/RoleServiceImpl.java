package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.Role;
import com.archer.admin.base.repository.RoleMapper;
import com.archer.admin.base.repository.RoleMenuMapper;
import com.archer.admin.base.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<Role> queryUserRoles(int userId) {
        return roleMenuMapper.selectUserRoles(userId);
    }
}
