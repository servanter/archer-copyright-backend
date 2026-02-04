package com.archer.admin.web.sku.controller;

import com.archer.admin.base.entities.Sku;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.sku.entities.SkuTransform.SkuBatchModifyStockReq;
import com.archer.admin.web.sku.entities.SkuTransform.SkuQueryReq;
import com.archer.admin.web.sku.entities.SkuTransform.SkuQueryRes;
import com.archer.admin.web.sku.entities.SkuTransform.SkuRes;
import com.archer.admin.web.sku.entities.SkuTransform.SkuStockDetailReq;
import com.archer.admin.web.sku.entities.SkuTransform.SkuStockDetailRes;
import com.archer.admin.web.sku.entities.SkuTransform.SquModifyStatusReq;
import com.archer.admin.web.sku.service.BizSkuService;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.WebContext;

@RequestMapping("/sku")
@RestController
@ResponseResultBody
public class SkuController {

    @Resource
    private BizSkuService bizSkuService;

    @RequestMapping("/detail/{skuId}")
    public SkuRes detail(WebContext webContext, @PathVariable("skuId") int skuId) {
        return bizSkuService.query(skuId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody Sku sku) {
        return bizSkuService.modify(sku);
    }

    @RequestMapping("/list")
    public SkuQueryRes list(WebContext webContext, SkuQueryReq skuQueryReq) {
        return bizSkuService.list(skuQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, String id) {
        return bizSkuService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody Sku sku) {
        return bizSkuService.save(sku);
    }

    @RequestMapping("/modifyStatus")
    public Result modifyStatus(WebContext webContext, @RequestBody SquModifyStatusReq req) {
        return bizSkuService.modifyStatus(req);
    }

    @RequestMapping("/batchModifyStock")
    public Result batchModifyStock(WebContext webContext, @RequestBody List<SkuBatchModifyStockReq> req) {
        return bizSkuService.batchModifyStock(req);
    }

    @RequestMapping("/queryDetailStock")
    public SkuStockDetailRes queryDetailStock(WebContext webContext, @RequestBody SkuStockDetailReq req) {
        return bizSkuService.queryDetailStock(req);
    }
}
