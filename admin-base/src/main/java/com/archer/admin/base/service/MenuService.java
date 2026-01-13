package com.archer.admin.base.service;

import com.archer.admin.base.entities.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuService extends IService<Menu> {

    List<Menu> queryRoleMenus(int roleId);
}
