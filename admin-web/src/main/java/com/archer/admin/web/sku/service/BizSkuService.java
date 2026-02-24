package com.archer.admin.web.sku.service;

import com.alibaba.fastjson2.JSONArray;
import com.archer.admin.base.entities.Channel;
import com.archer.admin.base.entities.ProductSpecValue;
import com.archer.admin.base.entities.Sku;
import com.archer.admin.base.entities.StockChangeLog;
import com.archer.admin.base.service.ChannelService;
import com.archer.admin.base.service.ProductSpecGroupService;
import com.archer.admin.base.service.ProductSpecValueService;
import com.archer.admin.base.service.SkuService;
import com.archer.admin.base.service.StockChangeLogService;
import com.archer.admin.web.channel.entities.ChannelTransform.ChannelRes;
import com.archer.admin.web.common.ValidEnum;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.component.WebContext;
import com.archer.admin.web.sku.entities.SkuTransform.ChannelStock;
import com.archer.admin.web.sku.entities.SkuTransform.SkuBatchModifyStockReq;
import com.archer.admin.web.sku.entities.SkuTransform.SkuQueryReq;
import com.archer.admin.web.sku.entities.SkuTransform.SkuQueryRes;
import com.archer.admin.web.sku.entities.SkuTransform.SkuRes;
import com.archer.admin.web.sku.entities.SkuTransform.SkuStockDetail;
import com.archer.admin.web.sku.entities.SkuTransform.SkuStockDetailReq;
import com.archer.admin.web.sku.entities.SkuTransform.SkuStockDetailRes;
import com.archer.admin.web.sku.entities.SkuTransform.SquModifyStatusReq;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizSkuService {

    @Resource
    private SkuService skuService;
    @Resource
    private ProductSpecValueService productSpecValueService;
    @Resource
    private ChannelService channelService;
    @Resource
    private StockChangeLogService stockChangeLogService;

    public SkuRes query(int skuId) {
        return SkuRes.parseRes(skuService.getById(skuId));
    }

    public Result modify(Sku sku) {
        return skuService.updateById(sku) ? Result.success() : Result.error();
    }

    public SkuQueryRes list(SkuQueryReq skuQueryReq) {
        Page<Sku> page = skuService.lambdaQuery()

                .like(StringUtils.isNotBlank(skuQueryReq.getProductId()), Sku::getProductId, skuQueryReq.getProductId())
                .like(StringUtils.isNotBlank(skuQueryReq.getSkuCode()), Sku::getSkuCode, skuQueryReq.getSkuCode())
                .like(StringUtils.isNotBlank(skuQueryReq.getSpecValueIds()), Sku::getSpecValueIds,
                        skuQueryReq.getSpecValueIds())

                .eq(skuQueryReq.getStatus() != 0, Sku::getStatus, skuQueryReq.getStatus())
                .eq(Sku::getValid, 1)
                .eq(skuQueryReq.getOperatorId() != 0, Sku::getOperatorId, skuQueryReq.getOperatorId())

                .page(new Page<>(skuQueryReq.getPageNo(), skuQueryReq.getPageSize()));

        List<SkuRes> list = getSkuResList(page.getRecords());

        return SkuQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public List<SkuRes> getSkuResList(List<Sku> skus) {
        List<String> valueIds = skus.stream()
                .flatMap(sku -> {
                    List<String> valuesIds = JSONArray.parseArray(sku.getSpecValueIds(), String.class);
                    return valuesIds.stream();
                })
                .collect(Collectors.toList());

        Map<String, ProductSpecValue> valueMap = productSpecValueService.lambdaQuery()
                .in(CollectionUtils.isNotEmpty(valueIds), ProductSpecValue::getId, valueIds)
                .list().stream().collect(Collectors.toMap(ProductSpecValue::getId, Function.identity()));

        List<SkuRes> list = skus.stream().map(sku -> {
            List<String> valuesIds = JSONArray.parseArray(sku.getSpecValueIds(), String.class);
            List<ProductSpecValue> filteredValues = valuesIds.stream()
                    .filter(valueId -> valueMap.containsKey(valueId))
                    .map(valueId -> valueMap.get(valueId))
                    .collect(Collectors.toList());
            return SkuRes.parseRes(sku, filteredValues);
        }).collect(Collectors.toList());
        return list;
    }

    public Result remove(String skuId) {
        Sku sku = new Sku();
        sku.setId(skuId);
        sku.setValid(-1);
        return skuService.updateById(sku) ? Result.success() : Result.error();
    }

    public Result save(Sku sku) {
        return skuService.save(sku) ? Result.success() : Result.error();
    }

    public Result modifyStatus(SquModifyStatusReq req) {
        Sku sku = new Sku();
        sku.setId(req.getId());
        sku.setStatus(req.getStatus());
        return skuService.updateById(sku) ? Result.success() : Result.error();
    }

    public Result batchModifyStock(WebContext webContext, List<SkuBatchModifyStockReq> req) {
        
        // 准备库存变更记录
        List<StockChangeLog> stockChangeLogs = new ArrayList<>();

        req.forEach(r -> {
            Sku sku = skuService.getById(r.getId());
            Sku updateSku = new Sku();
            updateSku.setId(r.getId());
            
            // 计算变更后的库存
            int newStock = r.getOperationType() == 1 ? sku.getTotalStock() + r.getQuantity()
                    : sku.getTotalStock() - r.getQuantity();
            updateSku.setTotalStock(newStock);
            
            // 记录库存变更
            StockChangeLog changeLog = new StockChangeLog();
            changeLog.setSkuId(r.getId());
            changeLog.setProductId(sku.getProductId());
            changeLog.setStock(r.getQuantity());
            changeLog.setType(r.getOperationType());  // 1表示增加，-1表示减少
            changeLog.setOperatorId(webContext.getUserId());
            changeLog.setValid(1);
            
            stockChangeLogs.add(changeLog);
            
            skuService.updateById(updateSku);
        });
        
        // 批量保存库存变更记录
        if (CollectionUtils.isNotEmpty(stockChangeLogs)) {
            stockChangeLogService.saveBatch(stockChangeLogs);
        }

        return Result.success();
    }

    public SkuStockDetailRes queryDetailStock(SkuStockDetailReq req) {
        // 所有渠道
        List<Channel> channels = channelService.lambdaQuery()
                .eq(Channel::getValid, ValidEnum.ACTIVE.getValue())
                .list();
        // sku列表
        List<Sku> skus = skuService.lambdaQuery()
                .eq(Sku::getProductId, req.getProductId())
                .eq(Sku::getValid, ValidEnum.ACTIVE.getValue())
                .list();

        List<SkuStockDetail> skuDetails = skus.stream()
                .map(sku -> toSkuStockDetail(sku, channels))
                .collect(Collectors.toList());

        return SkuStockDetailRes.builder()
                .channels(channels.stream().map(ChannelRes::parseRes).collect(Collectors.toList()))
                .list(skuDetails)
                .build();
    }

    private SkuStockDetail toSkuStockDetail(Sku sku, List<Channel> channels) {

        List<ChannelStock> channelStocks = channels.stream().map(channel -> {
            ChannelStock channelStock = ChannelStock.builder()
                    .id(channel.getId())
                    .name(channel.getChannelName())
                    .stock(1111)
                    .build();
            return channelStock;
        }).collect(Collectors.toList());

        List<SkuRes> list = getSkuResList(Lists.newArrayList(sku));
        return SkuStockDetail.builder()
                .skuId(sku.getId())
                .specValues(list.get(0).getSpecValues())
                .totalStock(sku.getTotalStock())
                .channelStocks(channelStocks)
                .build();
    }

}
