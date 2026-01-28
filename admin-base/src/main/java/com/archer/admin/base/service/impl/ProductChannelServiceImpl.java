package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.ProductChannel;
import com.archer.admin.base.repository.ProductChannelMapper;
import com.archer.admin.base.service.ProductChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductChannelServiceImpl extends ServiceImpl<ProductChannelMapper, ProductChannel> implements ProductChannelService {
}
