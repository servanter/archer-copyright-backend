package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.ProductSpecValue;
import com.archer.admin.base.repository.ProductSpecValueMapper;
import com.archer.admin.base.service.ProductSpecValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductSpecValueServiceImpl extends ServiceImpl<ProductSpecValueMapper, ProductSpecValue> implements ProductSpecValueService {
}
