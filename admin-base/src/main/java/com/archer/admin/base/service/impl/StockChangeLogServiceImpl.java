package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.StockChangeLog;
import com.archer.admin.base.repository.StockChangeLogMapper;
import com.archer.admin.base.service.StockChangeLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class StockChangeLogServiceImpl extends ServiceImpl<StockChangeLogMapper, StockChangeLog> implements StockChangeLogService {
}
