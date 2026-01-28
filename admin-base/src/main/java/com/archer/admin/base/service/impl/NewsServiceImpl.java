package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.News;
import com.archer.admin.base.repository.NewsMapper;
import com.archer.admin.base.service.NewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {
}
