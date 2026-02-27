package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.ProductOrder;
import com.archer.admin.base.repository.ProductOrderMapper;
import com.archer.admin.base.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder> implements ProductOrderService {
}
