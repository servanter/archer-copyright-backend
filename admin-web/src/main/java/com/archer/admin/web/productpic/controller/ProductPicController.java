package com.archer.admin.web.productpic.controller;

import com.archer.admin.base.entities.ProductPic;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.productpic.entities.ProductPicTransform.ProductPicQueryReq;
import com.archer.admin.web.productpic.entities.ProductPicTransform.ProductPicQueryRes;
import com.archer.admin.web.productpic.entities.ProductPicTransform.ProductPicRes;
import com.archer.admin.web.productpic.service.BizProductPicService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.WebContext;

@RequestMapping("/product/pic")
@RestController
@ResponseResultBody
public class ProductPicController {

    @Resource
    private BizProductPicService bizProductPicService;

    @RequestMapping("/detail/{productPicId}")
    public ProductPicRes detail(WebContext webContext, @PathVariable("productPicId") int productPicId) {
        return bizProductPicService.query(productPicId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody ProductPic productPic) {
        return bizProductPicService.modify(productPic);
    }

    @RequestMapping("/list")
    public ProductPicQueryRes list(WebContext webContext, ProductPicQueryReq productPicQueryReq) {
        return bizProductPicService.list(productPicQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, int id) {
        return bizProductPicService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody ProductPic productPic) {
        return bizProductPicService.save(productPic);
    }
}
