package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.Category;
import com.archer.admin.base.repository.CategoryMapper;
import com.archer.admin.base.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
