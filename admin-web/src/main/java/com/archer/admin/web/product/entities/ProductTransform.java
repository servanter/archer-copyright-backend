package com.archer.admin.web.product.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.Product;
import com.archer.admin.base.entities.ProductPic;
import com.archer.admin.base.entities.Sku;
import com.archer.admin.web.common.ValidEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;
import com.archer.admin.web.productchannel.entities.StockStrategyEnum;

public class ProductTransform {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProductRes {
        // ID

        private String id;
        // 所属IP

        private Integer copyrightId;
        // 商品名称

        private String productName;
        // 商品类目

        private Integer thirdCategoryId;
        // 价格设定

        private Integer priceType;
        // 固定价格

        private Double price;
        // 状态

        private Integer status;
        // 状态

        private Integer saleStatus;
        // 预计发货日期
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate shippingDate;
        // 销售渠道

        private String saleChannelIds;
        // 状态

        private Integer valid;
        // 创建时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

        private String copyrightName;

        private String firstProductImg;

        public static ProductRes parseRes(Product product, String copyrightName, List<ProductPic> pics) {
            return getProductResBuilder(product)
                    .copyrightName(copyrightName)
                    .firstProductImg(pics.stream().findFirst().map(ProductPic::getFileUrl).orElse(""))
                    .build();

        }

        public static ProductRes parseRes(Product product) {
            return getProductResBuilder(product)
                    .build();
        }

        private static ProductResBuilder getProductResBuilder(Product product) {
            return ProductRes.builder()
                    .id(product.getId())
                    .copyrightId(product.getCopyrightId())
                    .productName(product.getProductName())
                    .thirdCategoryId(product.getThirdCategoryId())
                    .priceType(product.getPriceType())
                    .price(product.getPrice())
                    .status(product.getStatus())
                    .saleStatus(product.getSaleStatus())
                    .shippingDate(product.getShippingDate())
                    .saleChannelIds(product.getSaleChannelIds())
                    .valid(product.getValid())
                    .createTime(product.getCreateTime())
                    .updateTime(product.getUpdateTime());
        }

        public String getPriceTypeStr() {
            return PriceTypeEnum.of(priceType).getLabel();
        }

        public String getStatusStr() {
            return StatusEnum.of(status).getLabel();
        }

        public String getSaleStatusStr() {
            return SaleStatusEnum.of(saleStatus).getLabel();
        }

        public String getValidStr() {
            return ValidEnum.of(valid).getLabel();
        }
    }

    @Data
    public static class ProductQueryReq extends PageReq {

        private int copyrightId;

        private String productName;

        private int thirdCategoryId;

        private int status;
    }

    @Data
    public static class ProductPostReq extends Product {

        private List<String> picMainUrls;

        private List<String> picIntroUrls;

    }

    @Data
    public static class ProductModifyStatusReq {

        private String id;

        private int status;
    }

    @Data
    public static class ProductSpecAggrReq {

        private String productId;

        /**
         * 规格组
         */
        private List<ProductSpecGroupTransform> specs;

        private List<SkuTransform> skus;
    }

    @Builder
    @Getter
    public static class ProductSpecAttrRes {

        private List<ProductSpecGroupTransform> specs;

        private List<SkuTransform> skus;
    }

    @Data
    public static class SkuTransform {
        private String id;
        private String skuCode;
        private List<String> specValueIds;
        private Double price;

        private Integer stock;

        private Integer freezeStock;
    }

    @Data
    public static class ProductSpecGroupTransform {
        private String id;
        private String name;

        private List<ProductSpecValueTransform> values;
    }

    @Data
    public static class ProductSpecValueTransform {
        private String id;
        private String name;

    }

    @SuperBuilder
    @Getter
    public static class ProductQueryRes extends PageRes {

        private List<ProductRes> list;
        @Default
        private List<Map<String, Object>> priceTypes = PriceTypeEnum.TOTALS;
        @Default
        private List<Map<String, Object>> statuss = StatusEnum.TOTALS;
        @Default
        private List<Map<String, Object>> saleStatuss = SaleStatusEnum.TOTALS;
    }

    @SuperBuilder
    @Getter
    public static class ProductStockQueryRes extends PageRes {

        private List<ProductStockRes> list;
        @Default
        private List<Map<String, Object>> priceTypes = PriceTypeEnum.TOTALS;
        @Default
        private List<Map<String, Object>> statuss = StatusEnum.TOTALS;
        @Default
        private List<Map<String, Object>> saleStatuss = SaleStatusEnum.TOTALS;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProductStockRes {

        @Delegate
        @JsonIgnore
        private ProductRes productRes;

        private int totalStock;

        private int totalFreezeStock;

        private int totalRemainStock;

        public static ProductStockRes parseRes(Product product, String copyrightName, List<ProductPic> orDefault, List<Sku> skus) {
            ProductRes productRes = ProductRes.parseRes(product, copyrightName, orDefault);
            int totalStock = skus.stream()
                    .map(Sku::getTotalStock)
                    .reduce(Integer::sum)
                    .orElse(0);
            int totalFreezeStock = skus.stream()
                    .map(Sku::getFreezeStock)
                    .reduce(Integer::sum)
                    .orElse(0);

            int totalRemainStock = 0;
            
            return ProductStockRes.builder()
                    .productRes(productRes)
                    .totalStock(totalStock)
                    .totalFreezeStock(totalFreezeStock)
                    .totalRemainStock(totalRemainStock)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ProductChannelConfigRes {
        private int id;
        private int channelId;
        private String channelName;
        private String platformProductId;
        private StockStrategyEnum stockStrategy;
    }

    @Builder
    @Getter
    public static class ProductChannelSkuConfigRes {
        private int id;
        private String skuId;
        private String specCombination;
        private int shareNum;
        private int lockNum;
        private int surplusNum;
    }

    @Data
    public static class SaveProductChannelConfig {
        private String productId;
        private List<PlatformProducts> platformProducts;
    }

    @Data
    public static class PlatformProducts {
        private int stockStrategy;
        private int channelId;
        private String platformProductId;
    }

    @Data
    public static class SaveProductChannelSkuConfig {
        private int id;
        private String productId;
        private String platformProductId;
        private int channelId;
        private int stockStrategy;
        private List<SkuLock> sku;
    }

    @Data
    public static class SkuLock {
        private String skuId;
        private int lockNum;
    }
}