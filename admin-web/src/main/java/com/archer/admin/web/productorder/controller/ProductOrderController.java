package com.archer.admin.web.productorder.controller;

import com.archer.admin.base.entities.ProductOrder;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.component.WebContext;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderEditReq;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderQueryReq;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderQueryRes;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderRes;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderShippingReq;
import com.archer.admin.web.productorder.service.BizProductOrderService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/productOrder")
@RestController
@ResponseResultBody
public class ProductOrderController {

    @Resource
    private BizProductOrderService bizProductOrderService;

    @RequestMapping("/detail/{product_orderId}")
    public ProductOrderRes detail(WebContext webContext, @PathVariable("product_orderId") String product_orderId) {
        return bizProductOrderService.query(product_orderId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody ProductOrder product_order) {
        return bizProductOrderService.modify(product_order);
    }

    @RequestMapping("/list")
    public ProductOrderQueryRes list(WebContext webContext, ProductOrderQueryReq req) {
        return bizProductOrderService.list(req);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, String id) {
        return bizProductOrderService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody ProductOrderEditReq req) {
        return bizProductOrderService.save(req);
    }

    @RequestMapping(value = "/ship", method = RequestMethod.POST)
    public Result ship(WebContext webContext, @RequestBody ProductOrderShippingReq req) {
        return bizProductOrderService.ship(req);
    }
}
