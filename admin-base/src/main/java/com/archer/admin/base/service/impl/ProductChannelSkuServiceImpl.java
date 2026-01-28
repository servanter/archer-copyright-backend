package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.ProductChannelSku;
import com.archer.admin.base.repository.ProductChannelSkuMapper;
import com.archer.admin.base.service.ProductChannelSkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductChannelSkuServiceImpl extends ServiceImpl<ProductChannelSkuMapper, ProductChannelSku> implements ProductChannelSkuService {
}
