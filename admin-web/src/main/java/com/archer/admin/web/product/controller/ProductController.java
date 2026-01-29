package com.archer.admin.web.product.controller;

import com.archer.admin.base.entities.Product;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.product.entities.ProductTransform;
import com.archer.admin.web.product.entities.ProductTransform.ProductChannelConfigRes;
import com.archer.admin.web.product.entities.ProductTransform.ProductChannelSkuConfigRes;
import com.archer.admin.web.product.entities.ProductTransform.ProductModifyStatusReq;
import com.archer.admin.web.product.entities.ProductTransform.ProductPostReq;
import com.archer.admin.web.product.entities.ProductTransform.ProductQueryReq;
import com.archer.admin.web.product.entities.ProductTransform.ProductQueryRes;
import com.archer.admin.web.product.entities.ProductTransform.ProductRes;
import com.archer.admin.web.product.entities.ProductTransform.ProductSpecAttrRes;
import com.archer.admin.web.product.entities.ProductTransform.SaveProductChannelSkuConfig;
import com.archer.admin.web.product.entities.ProductTransform.SaveProductChannelConfig;
import com.archer.admin.web.product.service.BizProductService;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.WebContext;

@RequestMapping("/product")
@RestController
@ResponseResultBody
public class ProductController {

    @Resource
    private BizProductService bizProductService;

    @RequestMapping("/detail/{productId}")
    public ProductRes detail(WebContext webContext, @PathVariable("productId") String productId) {
        return bizProductService.query(productId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody ProductPostReq productPostReq) {
        return bizProductService.modify(productPostReq);
    }

    @RequestMapping("/list")
    public ProductQueryRes list(WebContext webContext, ProductQueryReq productQueryReq) {
        return bizProductService.list(productQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, String id) {
        return bizProductService.remove(id);
    }

    @RequestMapping("/modifyStatus")
    public Result modifyStatus(WebContext webContext, ProductModifyStatusReq productModifyStatusReq) {
        return bizProductService.modifyStatus(productModifyStatusReq);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody ProductPostReq productPostReq) {
        return bizProductService.save(productPostReq);
    }

    @RequestMapping(value = "/settingStock", method = RequestMethod.POST)
    public Result settingStock(WebContext webContext,
            @RequestBody ProductTransform.ProductSpecAggrReq productSpecAggrReq) {
        return bizProductService.settingSpecAndStock(webContext, productSpecAggrReq);
    }

    @RequestMapping(value = "/getSpecAndStock")
    public ProductSpecAttrRes getSpecAndStock(WebContext webContext, @RequestParam("productId") String productId) {
        return bizProductService.getSpecAndStock(webContext, productId);
    }

    /**
     * 获取产品渠道配置列表
     * @param webContext
     * @param productId
     * @return
     */
    @RequestMapping(value = "/queryProductChannelConfigList")
    public List<ProductChannelConfigRes> queryProductChannelConfigList(WebContext webContext,
            @RequestParam("productId") String productId) {
        return bizProductService.queryProductChannelConfigList(webContext, productId);
    }

    /**
     * 查询指定产品和渠道的SKU配置列表
     * 
     * @param webContext 用户上下文信息
     * @param productId  产品ID
     * @param channelId  渠道ID
     * @return 产品渠道SKU配置列表
     */
    @RequestMapping(value = "/queryProductChannelSkuList")
    public List<ProductChannelSkuConfigRes> queryProductChannelSkuList(WebContext webContext,
            @RequestParam("productId") String productId, @RequestParam("channelId") int channelId) {
        return bizProductService.queryProductChannelSkuList(webContext, productId, channelId);
    }

    /**
     * 保存渠道SKU锁定配置
     * 
     * @param webContext               用户上下文信息
     * @param saveChannelSkuLockConfig 渠道SKU锁定配置信息
     * @return 操作结果
     */
    @RequestMapping(value = "/saveChannelSkuLockConfig")
    public Result saveChannelSkuLock(WebContext webContext,
            @RequestBody SaveProductChannelSkuConfig saveChannelSkuLockConfig) {
        return bizProductService.saveChannelSkuLockConfig(webContext, saveChannelSkuLockConfig);
    }

}
