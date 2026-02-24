package com.archer.admin.web.stock.service;

import com.alibaba.fastjson2.JSONArray;
import com.archer.admin.base.entities.Product;
import com.archer.admin.base.entities.ProductChannelSku;
import com.archer.admin.base.entities.ProductSpecValue;
import com.archer.admin.base.entities.Sku;
import com.archer.admin.base.entities.StockChangeLog;
import com.archer.admin.base.service.ProductChannelSkuService;
import com.archer.admin.base.service.ProductService;
import com.archer.admin.base.service.ProductSpecValueService;
import com.archer.admin.base.service.SkuService;
import com.archer.admin.base.service.StockChangeLogService;
import com.archer.admin.web.channel.entities.ChannelTransform.ChannelQueryReq;
import com.archer.admin.web.channel.entities.ChannelTransform.ChannelQueryRes;
import com.archer.admin.web.channel.service.BizChannelService;
import com.archer.admin.web.component.WebContext;
import com.archer.admin.web.stock.entities.StockTransform.ChannelStock;
import com.archer.admin.web.stock.entities.StockTransform.ChannelStockInfo;
import com.archer.admin.web.stock.entities.StockTransform.SkuChangeLogInfo;
import com.archer.admin.web.stock.entities.StockTransform.SkuChangeLogRes;
import com.archer.admin.web.stock.entities.StockTransform.StockDetailInfo;
import com.archer.admin.web.stock.entities.StockTransform.StockDetailRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.CollectionUtils;

@Service
public class BizStockService {

    @Resource
    private BizChannelService bizChannelService;
    
    @Resource
    private SkuService skuService;
    
    @Resource
    private ProductChannelSkuService productChannelSkuService;
    
    @Resource
    private ProductSpecValueService productSpecValueService;
    
    @Resource
    private ProductService productService;
    
    @Resource
    private StockChangeLogService stockChangeLogService;

    public StockDetailRes queryDetail(WebContext webContext, String productId) {
        // 获取产品关联的有效渠道
        List<Integer> productChannelIds = productChannelSkuService.lambdaQuery()
                .eq(ProductChannelSku::getProductId, productId)
                .ne(ProductChannelSku::getLockNum, 0)
                .list()
                .stream()
                .map(ProductChannelSku::getChannelId)
                .distinct()
                .collect(Collectors.toList());
        
        // 获取渠道信息
        List<ChannelStock> channels = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(productChannelIds)) {
            ChannelQueryReq channelQueryReq = new ChannelQueryReq();
            channelQueryReq.setPageNo(1);
            channelQueryReq.setPageSize(100);
            ChannelQueryRes channelQueryRes = bizChannelService.list(channelQueryReq);
            
            channels = channelQueryRes.getList().stream()
                    .filter(channel -> productChannelIds.contains(channel.getId()))
                    .map(channel -> ChannelStock.builder()
                            .id(String.valueOf(channel.getId()))
                            .name(channel.getChannelName())
                            .build())
                    .collect(Collectors.toList());
        }
        
        // 获取产品的所有SKU
        List<Sku> skuList = skuService.lambdaQuery()
                .eq(Sku::getProductId, productId)
                .eq(Sku::getValid, 1)
                .list();
        
        // 获取所有规格值
        List<String> specValueIds = skuList.stream()
                .flatMap(sku -> {
                    List<String> valuesIds = JSONArray.parseArray(sku.getSpecValueIds(), String.class);
                    return valuesIds.stream();
                })
                .collect(Collectors.toList());
        
        final Map<String, ProductSpecValue> specValueMap;
        if (CollectionUtils.isNotEmpty(specValueIds)) {
            specValueMap = productSpecValueService.lambdaQuery()
                    .in(ProductSpecValue::getId, specValueIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(ProductSpecValue::getId, v -> v));
        } else {
            specValueMap = new HashMap<>();
        }
        
        // 获取所有渠道SKU配置
        List<String> skuIds = skuList.stream()
                .map(Sku::getId)
                .collect(Collectors.toList());
        
        List<ProductChannelSku> productChannelSkus = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(skuIds)) {
            productChannelSkus = productChannelSkuService.lambdaQuery()
                    .eq(ProductChannelSku::getProductId, productId)
                    .in(ProductChannelSku::getSkuId, skuIds)
                    .eq(ProductChannelSku::getValid, 1)
                    .list();
        }
        
        // 按SKU分组渠道库存
        Map<String, List<ProductChannelSku>> skuChannelMap = productChannelSkus.stream()
                .collect(Collectors.groupingBy(ProductChannelSku::getSkuId));
        
        // 构建库存详情列表
        List<StockDetailInfo> stockDetailList = new ArrayList<>();
        for (Sku sku : skuList) {
            // 构建规格值字符串
            String specValues = "";
            if (sku.getSpecValueIds() != null && !sku.getSpecValueIds().isEmpty()) {
                List<String> valueIds = JSONArray.parseArray(sku.getSpecValueIds(), String.class);
                specValues = valueIds.stream()
                        .map(id -> specValueMap.get(id) != null ? specValueMap.get(id).getSpecValue() : "")
                        .collect(Collectors.joining("，"));
            }
            
            // 构建渠道库存列表，只包含有库存的渠道
            List<ChannelStockInfo> channelStocks = new ArrayList<>();
            List<ProductChannelSku> skuChannelStocks = skuChannelMap.getOrDefault(sku.getId(), new ArrayList<>());
            
            // 过滤出有库存的渠道SKU
            List<ProductChannelSku> stockChannelSkus = skuChannelStocks.stream()
                    .filter(skuChannel -> skuChannel.getLockNum() != null && skuChannel.getLockNum() > 0)
                    .collect(Collectors.toList());
            
            // 转换为渠道库存信息
            for (ProductChannelSku skuChannel : stockChannelSkus) {
                // 查找渠道信息
                ChannelStock channel = channels.stream()
                        .filter(c -> c.getId().equals(String.valueOf(skuChannel.getChannelId())))
                        .findFirst()
                        .orElse(null);
                
                if (channel != null) {
                    channelStocks.add(ChannelStockInfo.builder()
                            .id(channel.getId())
                            .name(channel.getName())
                            .stock(skuChannel.getLockNum())
                            .build());
                }
            }
            
            stockDetailList.add(StockDetailInfo.builder()
                    .id(sku.getId())
                    .specValues(specValues)
                    .totalStock(sku.getTotalStock())
                    .channelStocks(channelStocks)
                    .build());
        }

        return StockDetailRes.builder()
                .channels(channels)
                .stockDetailList(stockDetailList)
                .build();
    }
    
    /**
     * 查询SKU变更记录
     * @param webContext 用户上下文
     * @param productId 产品ID
     * @return SKU变更记录响应
     */
    public SkuChangeLogRes querySkuChangeLog(WebContext webContext, String productId) {
        // 获取产品信息
        Product product = productService.getById(productId);
        String productName = product != null ? product.getProductName() : "";
        
        // 从StockChangeLog表中查询变更记录
        List<StockChangeLog> stockChangeLogs = stockChangeLogService.lambdaQuery()
                .eq(StockChangeLog::getProductId, productId)
                .eq(StockChangeLog::getValid, 1)
                .orderByDesc(StockChangeLog::getCreateTime)
                .list();
        
        // 获取所有涉及到的SKU
        List<String> skuIds = stockChangeLogs.stream()
                .map(StockChangeLog::getSkuId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<String, Sku> skuMap = new HashMap<>();
        Map<String, String> skuSpecValuesMap = new HashMap<>();
        
        if (CollectionUtils.isNotEmpty(skuIds)) {
            // 获取SKU信息
            skuMap = skuService.lambdaQuery()
                    .in(Sku::getId, skuIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Sku::getId, s -> s));
            
            // 获取所有规格值
            List<String> specValueIds = skuMap.values().stream()
                    .flatMap(sku -> {
                        if (sku.getSpecValueIds() != null && !sku.getSpecValueIds().isEmpty()) {
                            List<String> valuesIds = JSONArray.parseArray(sku.getSpecValueIds(), String.class);
                            return valuesIds.stream();
                        }
                        return new ArrayList<String>().stream();
                    })
                    .collect(Collectors.toList());
            
            final Map<String, ProductSpecValue> specValueMap;
            if (CollectionUtils.isNotEmpty(specValueIds)) {
                specValueMap = productSpecValueService.lambdaQuery()
                        .in(ProductSpecValue::getId, specValueIds)
                        .list()
                        .stream()
                        .collect(Collectors.toMap(ProductSpecValue::getId, v -> v));
            } else {
                specValueMap = new HashMap<>();
            }
            
            // 构建SKU规格值字符串映射
            for (Map.Entry<String, Sku> entry : skuMap.entrySet()) {
                String skuId = entry.getKey();
                Sku sku = entry.getValue();
                
                String specValues = "";
                if (sku.getSpecValueIds() != null && !sku.getSpecValueIds().isEmpty()) {
                    List<String> valueIds = JSONArray.parseArray(sku.getSpecValueIds(), String.class);
                    specValues = valueIds.stream()
                            .map(id -> specValueMap.get(id) != null ? specValueMap.get(id).getSpecValue() : "")
                            .collect(Collectors.joining("，"));
                }
                skuSpecValuesMap.put(skuId, specValues);
            }
        }
        
        // 构建变更记录列表
        List<SkuChangeLogInfo> changeLogList = new ArrayList<>();
        for (StockChangeLog log : stockChangeLogs) {
            String skuId = log.getSkuId();
            String specValues = skuSpecValuesMap.getOrDefault(skuId, "");
            
            // 根据类型设置操作描述
            String typeDesc = "";
            if (log.getType() != null) {
                typeDesc = log.getType() == 1 ? "增" : "删";
            }
            
        
            changeLogList.add(SkuChangeLogInfo.builder()
                    .id(String.valueOf(log.getId()))
                    .specValues(specValues)
                    .skuId(skuId)
                    .type(log.getType())
                    .stock(log.getStock())
                    .typeDesc(typeDesc) 
                    .createTime(log.getCreateTime())
                    .updateTime(log.getUpdateTime())
                    .build());
        }
        
        return SkuChangeLogRes.builder()
                .productName(productName)
                .productId(productId)
                .changeLogList(changeLogList)
                .build();
    }
}
