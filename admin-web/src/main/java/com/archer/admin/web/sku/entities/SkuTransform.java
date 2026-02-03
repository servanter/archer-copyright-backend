package com.archer.admin.web.sku.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.ProductSpecValue;
import com.archer.admin.base.entities.Sku;
import com.archer.admin.web.common.ValidEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

public class SkuTransform {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SkuRes {
        // ID

        private String id;
        // 关联商品 ID

        private String productId;
        // SKU 货号

        private String skuCode;
        // 关联规格值 ID JSON

        private String specValueIds;
        // SKU 独立价格

        private Double price;
        // 总库存

        private Integer totalStock;
        // 冻结库存

        private Integer freezeStock;
        // 状态

        private Integer status;
        // 状态

        private Integer valid;
        // 操作人

        private Integer operatorId;
        // 创建时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;
        // 规格值
        private String specValues;

        public static SkuRes parseRes(Sku sku) {
            return SkuRes.builder()
                    .id(sku.getId())
                    .productId(sku.getProductId())
                    .skuCode(sku.getSkuCode())
                    .specValueIds(sku.getSpecValueIds())
                    .price(sku.getPrice())
                    .totalStock(sku.getTotalStock())
                    .freezeStock(sku.getFreezeStock())
                    .status(sku.getStatus())
                    .valid(sku.getValid())
                    .operatorId(sku.getOperatorId())
                    .createTime(sku.getCreateTime())
                    .updateTime(sku.getUpdateTime())
                    .build();
        }

        public static SkuRes parseRes(Sku sku, List<ProductSpecValue> values) {
            return SkuRes.builder()
                    .id(sku.getId())
                    .productId(sku.getProductId())
                    .skuCode(sku.getSkuCode())
                    .specValueIds(sku.getSpecValueIds())
                    .price(sku.getPrice())
                    .totalStock(sku.getTotalStock())
                    .freezeStock(sku.getFreezeStock())
                    .status(sku.getStatus())
                    .valid(sku.getValid())
                    .operatorId(sku.getOperatorId())
                    .createTime(sku.getCreateTime())
                    .updateTime(sku.getUpdateTime())
                    .specValues(values.stream().map(ProductSpecValue::getSpecValue).collect(Collectors.joining("，")))
                    .build();

        }

        public String getStatusStr() {
            return StatusEnum.of(status).getLabel();
        }

        public String getValidStr() {
            return ValidEnum.of(valid).getLabel();
        }
    }

    @Data
    public static class SkuQueryReq extends PageReq {

        private String productId;

        private String skuCode;

        private String specValueIds;

        private int status;

        private int operatorId;
    }

    @SuperBuilder
    @Getter
    public static class SkuQueryRes extends PageRes {
        private List<SkuRes> list;
        @Default
        private List<Map<String, Object>> statuss = StatusEnum.TOTALS;
    }

    @Data
    public static class SquModifyStatusReq {
        private String id;
        private int status;
    }

    @Data
    public static class SkuBatchModifyStockReq {
        private String id;
        private int operationType;
        private int quantity;
    }
}