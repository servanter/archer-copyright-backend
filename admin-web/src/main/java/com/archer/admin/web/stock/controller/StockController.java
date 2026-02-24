package com.archer.admin.web.stock.controller;

import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.WebContext;
import com.archer.admin.web.stock.entities.StockTransform.SkuChangeLogRes;
import com.archer.admin.web.stock.entities.StockTransform.StockDetailRes;
import com.archer.admin.web.stock.service.BizStockService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/stock")
@RestController
@ResponseResultBody
public class StockController {

    @Resource
    private BizStockService bizStockService;

    @RequestMapping(value = "/detail/{productId}")
    public StockDetailRes queryDetail(WebContext webContext, @PathVariable("productId") String productId) {
        return bizStockService.queryDetail(webContext, productId);
    }
    
    @RequestMapping(value = "/changeLog/{productId}")
    public SkuChangeLogRes querySkuChangeLog(WebContext webContext, @PathVariable("productId") String productId) {
        return bizStockService.querySkuChangeLog(webContext, productId);
    }

}
