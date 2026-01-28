package com.archer.admin.base.repository;

import com.archer.admin.base.entities.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

    @Select("SELECT m.* FROM menu m INNER JOIN role_menu rm INNER JOIN user_role ru "
            + "WHERE m.id = rm.menu_id AND rm.role_id = ru.role_id AND ru.user_id = ${userId} AND m.valid = 1 AND m.parent_id <= 0")
    public List<Menu> selectByUserId(int userId);

    @Select("SELECT m.* FROM menu m INNER JOIN role_menu rm WHERE m.id = rm.menu_id AND m.valid = 1 AND rm.role_id = #{roleId}")
    List<Menu> getRoleMenus(int roleId);
}
