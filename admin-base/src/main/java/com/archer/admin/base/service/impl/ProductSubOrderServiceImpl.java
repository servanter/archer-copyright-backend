package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.ProductSubOrder;
import com.archer.admin.base.repository.ProductSubOrderMapper;
import com.archer.admin.base.service.ProductSubOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductSubOrderServiceImpl extends ServiceImpl<ProductSubOrderMapper, ProductSubOrder> implements ProductSubOrderService {
}
