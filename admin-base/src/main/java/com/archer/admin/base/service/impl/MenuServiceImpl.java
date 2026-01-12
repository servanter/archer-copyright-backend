package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.Menu;
import com.archer.admin.base.repository.MenuMapper;
import com.archer.admin.base.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
}
