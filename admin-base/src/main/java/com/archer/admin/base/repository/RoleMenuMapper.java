package com.archer.admin.base.repository;

import com.archer.admin.base.entities.Role;
import com.archer.admin.base.entities.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Select("SELECT r.* FROM role r INNER JOIN user_role ur WHERE ur.role_id = r.id AND ur.user_id = #{userId} AND r.valid = 1")
    List<Role> selectUserRoles(int userId);
}
