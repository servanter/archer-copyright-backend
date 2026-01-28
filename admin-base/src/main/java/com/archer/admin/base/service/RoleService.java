package com.archer.admin.base.service;

import com.archer.admin.base.entities.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleService extends IService<Role> {

    List<Role> queryUserRoles(int userId);
}
