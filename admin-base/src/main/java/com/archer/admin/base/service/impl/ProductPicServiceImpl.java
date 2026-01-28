package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.ProductPic;
import com.archer.admin.base.repository.ProductPicMapper;
import com.archer.admin.base.service.ProductPicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductPicServiceImpl extends ServiceImpl<ProductPicMapper, ProductPic> implements ProductPicService {
}
