package com.archer.admin.web.news.controller;

import com.archer.admin.base.entities.News;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.component.WebContext;
import com.archer.admin.web.news.entities.NewsTransform.NewsQueryReq;
import com.archer.admin.web.news.entities.NewsTransform.NewsQueryRes;
import com.archer.admin.web.news.entities.NewsTransform.NewsRes;
import com.archer.admin.web.news.service.BizNewsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/news")
@RestController
@ResponseResultBody
public class NewsController {

    @Resource
    private BizNewsService bizNewsService;

    @RequestMapping("/detail/{newsId}")
    public NewsRes detail(WebContext webContext, @PathVariable("newsId") int newsId) {
        return bizNewsService.query(newsId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody News news) {
        return bizNewsService.modify(news);
    }

    @RequestMapping("/list")
    public NewsQueryRes list(WebContext webContext, NewsQueryReq newsQueryReq) {
        return bizNewsService.list(newsQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, int id) {
        return bizNewsService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody News news) {
        return bizNewsService.save(news);
    }
}
