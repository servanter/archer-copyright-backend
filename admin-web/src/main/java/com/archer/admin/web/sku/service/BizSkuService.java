package com.archer.admin.web.sku.service;

import com.archer.admin.base.entities.Sku;
import com.archer.admin.base.service.SkuService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.sku.entities.SkuTransform.SkuQueryReq;
import com.archer.admin.web.sku.entities.SkuTransform.SkuQueryRes;
import com.archer.admin.web.sku.entities.SkuTransform.SkuRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizSkuService {

    @Resource
    private SkuService skuService;

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
.like(StringUtils.isNotBlank(skuQueryReq.getSpecValueIds()), Sku::getSpecValueIds, skuQueryReq.getSpecValueIds())



.eq(skuQueryReq.getStatus() != 0, Sku::getStatus, skuQueryReq.getStatus())
.eq(Sku::getValid, 1)
.eq(skuQueryReq.getOperatorId() != 0, Sku::getOperatorId, skuQueryReq.getOperatorId())

                .page(new Page<>(skuQueryReq.getPageNo(), skuQueryReq.getPageSize()));

        List<SkuRes> list = page.getRecords().stream().map(SkuRes::parseRes).collect(Collectors.toList());
        return SkuQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
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
}
