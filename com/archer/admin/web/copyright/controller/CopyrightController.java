package com.archer.admin.web.copyright.controller;

import com.archer.admin.base.entities.Copyright;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.copyright.entities.CopyrightTransform.CopyrightQueryReq;
import com.archer.admin.web.copyright.entities.CopyrightTransform.CopyrightQueryRes;
import com.archer.admin.web.copyright.entities.CopyrightTransform.CopyrightRes;
import com.archer.admin.web.copyright.service.BizCopyrightService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.WebContext;

@RequestMapping("/copyright")
@RestController
@ResponseResultBody
public class CopyrightController {

    @Resource
    private BizCopyrightService bizCopyrightService;

    @RequestMapping("/detail/{copyrightId}")
    public CopyrightRes detail(WebContext webContext, @PathVariable("copyrightId") int copyrightId) {
        return bizCopyrightService.query(copyrightId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody Copyright copyright) {
        return bizCopyrightService.modify(copyright);
    }

    @RequestMapping("/list")
    public CopyrightQueryRes list(WebContext webContext, CopyrightQueryReq copyrightQueryReq) {
        return bizCopyrightService.list(copyrightQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, int id) {
        return bizCopyrightService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody Copyright copyright) {
        return bizCopyrightService.save(copyright);
    }
}
