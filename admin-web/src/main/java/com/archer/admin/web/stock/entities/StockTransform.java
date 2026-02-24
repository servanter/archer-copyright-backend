package com.archer.admin.web.stock.entities;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class StockTransform {
    
    @Getter
    @Builder
    public static class StockDetailRes {
        // 渠道列表
        private List<ChannelStock> channels;
        // 库存详情列表
        private List<StockDetailInfo> stockDetailList;
    }
    
    @Getter
    @Builder
    public static class ChannelStock {
        // 渠道ID
        private String id;
        // 渠道名称
        private String name;
    }
    
    @Getter
    @Builder
    public static class StockDetailInfo {
        // SKU ID
        private String id;
        // 规格值组合
        private String specValues;
        // 总库存
        private Integer totalStock;
        // 渠道库存列表
        private List<ChannelStockInfo> channelStocks;
    }
    
    @Getter
    @Builder
    public static class ChannelStockInfo {
        // 渠道ID
        private String id;
        // 渠道名称
        private String name;
        // 渠道库存
        private Integer stock;
    }
    
    @Getter
    @Builder
    public static class SkuChangeLogRes {
        // 商品名称
        private String productName;
        // 商品ID
        private String productId;
        // 变更记录列表
        private List<SkuChangeLogInfo> changeLogList;
    }
    
    @Getter
    @Builder
    public static class SkuChangeLogInfo {
        // 日志ID
        private String id;
        // SKU名称（规格值组合）
        private String specValues;
        // SKU ID
        private String skuId;
        // 操作类型（1新增；-1删除）
        private Integer type;
        // 库存变更
        private Integer stock;
        // 操作类型描述
        private String typeDesc;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
    }
}
