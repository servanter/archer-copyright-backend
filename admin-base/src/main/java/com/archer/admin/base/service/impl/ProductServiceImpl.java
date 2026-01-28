package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.Product;
import com.archer.admin.base.repository.ProductMapper;
import com.archer.admin.base.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
}
