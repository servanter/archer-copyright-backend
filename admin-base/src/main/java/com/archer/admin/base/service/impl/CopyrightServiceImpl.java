package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.Copyright;
import com.archer.admin.base.repository.CopyrightMapper;
import com.archer.admin.base.service.CopyrightService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CopyrightServiceImpl extends ServiceImpl<CopyrightMapper, Copyright> implements CopyrightService {
}
