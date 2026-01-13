package com.archer.admin.web.news.service;

import com.archer.admin.base.entities.News;
import com.archer.admin.base.service.NewsService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.news.entities.NewsTransform.NewsQueryReq;
import com.archer.admin.web.news.entities.NewsTransform.NewsQueryRes;
import com.archer.admin.web.news.entities.NewsTransform.NewsRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BizNewsService {

    @Resource
    private NewsService newsService;

    public NewsRes query(int newsId) {
        return NewsRes.parseRes(newsService.getById(newsId));
    }

    public Result modify(News news) {
        return newsService.updateById(news) ? Result.success() : Result.error();
    }

    public NewsQueryRes list(NewsQueryReq newsQueryReq) {
        Page<News> page = newsService.lambdaQuery()
                
.like(StringUtils.isNotBlank(newsQueryReq.getTitle()), News::getTitle, newsQueryReq.getTitle())
.like(StringUtils.isNotBlank(newsQueryReq.getContent()), News::getContent, newsQueryReq.getContent())
.eq(newsQueryReq.getOperatorId() != 0, News::getOperatorId, newsQueryReq.getOperatorId())
.eq(News::getValid, 1)

                .page(new Page<>(newsQueryReq.getPageNo(), newsQueryReq.getPageSize()));

        List<NewsRes> list = page.getRecords().stream().map(NewsRes::parseRes).collect(Collectors.toList());
        return NewsQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(int newsId) {
        News news = new News();
        news.setId(newsId);
        news.setValid(-1);
        return newsService.updateById(news) ? Result.success() : Result.error();
    }

    public Result save(News news) {
        return newsService.save(news) ? Result.success() : Result.error();
    }
}
