package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.Sku;
import com.archer.admin.base.repository.SkuMapper;
import com.archer.admin.base.service.SkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {
}
