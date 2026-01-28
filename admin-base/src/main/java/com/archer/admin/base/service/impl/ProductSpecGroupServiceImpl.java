package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.ProductSpecGroup;
import com.archer.admin.base.repository.ProductSpecGroupMapper;
import com.archer.admin.base.service.ProductSpecGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductSpecGroupServiceImpl extends ServiceImpl<ProductSpecGroupMapper, ProductSpecGroup> implements ProductSpecGroupService {
}
