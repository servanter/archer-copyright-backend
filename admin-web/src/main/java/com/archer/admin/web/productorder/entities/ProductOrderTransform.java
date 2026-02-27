package com.archer.admin.web.productorder.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.ProductOrder;
import com.archer.admin.web.common.ValidEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

public class ProductOrderTransform {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProductOrderRes {
        // ID
        private String id;
        // 总数量
        private Double totalCount;
        // 总价格
        private Double totalPrice;
        // 来源
        private Integer source;
        // 物流状态
        private Integer logisticsStatus;
        // 采购方
        private String purchaser;
        // 采购合同文件
        private String purchaseContractFile;
        // 收件人
        private String receiver;
        // 收件人手机号
        private String receiverPhone;
        // 收件人省份
        private String receiverProvince;
        // 收件人城市
        private String receiverCity;
        // 收件人区域
        private String receiverArea;
        // 收件人地址
        private String receiverAddress;
        // 操作人
        private Integer operatorId;
        // 状态
        private Integer valid;
        // 创建时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;
        // 订单详情列表
        private List<ProductOrderDetailRes> details;

        public static ProductOrderRes parseRes(ProductOrder productorder) {
            return ProductOrderRes.builder()
                    .id(productorder.getId())
                    .totalCount(productorder.getTotalCount())
                    .totalPrice(productorder.getTotalPrice())
                    .source(productorder.getSource())
                    .logisticsStatus(productorder.getLogisticsStatus())
                    .purchaser(productorder.getPurchaser())
                    .purchaseContractFile(productorder.getPurchaseContractFile())
                    .receiver(productorder.getReceiver())
                    .receiverPhone(productorder.getReceiverPhone())
                    .receiverProvince(productorder.getReceiverProvince())
                    .receiverCity(productorder.getReceiverCity())
                    .receiverArea(productorder.getReceiverArea())
                    .receiverAddress(productorder.getReceiverAddress())
                    .operatorId(productorder.getOperatorId())
                    .valid(productorder.getValid())
                    .createTime(productorder.getCreateTime())
                    .updateTime(productorder.getUpdateTime())
                    .build();
        }

        public static ProductOrderRes parseRes(ProductOrder productorder, List<ProductOrderDetailRes> details) {
            return ProductOrderRes.builder()
                    .id(productorder.getId())
                    .totalCount(productorder.getTotalCount())
                    .totalPrice(productorder.getTotalPrice())
                    .source(productorder.getSource())
                    .logisticsStatus(productorder.getLogisticsStatus())
                    .purchaser(productorder.getPurchaser())
                    .purchaseContractFile(productorder.getPurchaseContractFile())
                    .receiver(productorder.getReceiver())
                    .receiverPhone(productorder.getReceiverPhone())
                    .receiverProvince(productorder.getReceiverProvince())
                    .receiverCity(productorder.getReceiverCity())
                    .receiverArea(productorder.getReceiverArea())
                    .receiverAddress(productorder.getReceiverAddress())
                    .operatorId(productorder.getOperatorId())
                    .valid(productorder.getValid())
                    .createTime(productorder.getCreateTime())
                    .updateTime(productorder.getUpdateTime())
                    .details(details)
                    .build();
        }

        public String getSourceStr() {
            return SourceEnum.of(source).getLabel();
        }

        /**
 * 获取物流状态的字符串表示
 * @return 物流状态对应的描述字符串，当状态为WAIT_DELIVERY时返回"未知"，其他状态返回对应的标签
 */
public String getLogisticsStatusStr() {
            LogisticsStatusEnum logisticsStatusEnum = LogisticsStatusEnum.of(logisticsStatus);
            if(logisticsStatusEnum == LogisticsStatusEnum.WAIT_DELIVERY) {
                if(CollectionUtils.isNotEmpty(this.details)) {
                    boolean hasDelivered = this.details.stream()
                            .anyMatch(detail -> detail.getLogisticsStatus() == LogisticsStatusEnum.DELIVERED.getValue());
                    if(hasDelivered) {
                        return LogisticsStatusEnum.PART_DELIVERED.getLabel();
                    }
                }
            }
            return logisticsStatusEnum.getLabel();
        }

        public String getValidStr() {
            return ValidEnum.of(valid).getLabel();
        }
    }

    @Data
    public static class ProductOrderShippingReq {
        // 订单ID
        private String orderId;
        // 发货类型，1是整单发货，2是拆单发货
        private Integer shippingType;
        // 子订单列表（拆单发货时使用）
        private List<ProductOrderShippingItem> products;
        // 物流公司
        private String logisticsCompany;
        // 物流单号
        private String logisticsTrackingNumber;
    }

    @Data
    public static class ProductOrderShippingItem {
        // 子订单ID
        private String subOrderId;
        // 发货数量
        private Integer quantity;
    }

    @Data
    public static class ProductOrderQueryReq extends PageReq {

        private String id;

        private int source;

        private int logisticsStatus;

        private int operatorId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ProductOrderEditReq {
        // ID
        private String id;
        // 采购方
        private String purchaser;
        // 采购合同文件
        private String purchaseContractFile;
        // 收件人
        private String receiver;
        // 收件人手机号
        private String receiverPhone;
        // 收件人省份
        private String receiverProvince;
        // 收件人城市
        private String receiverCity;
        // 收件人区域
        private String receiverArea;
        // 收件人地址
        private String receiverAddress;

        private List<ProductOrderSelectProductReq> purchaseProducts;
        // 操作人
        private Integer operatorId;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ProductOrderSelectProductReq {
        private String productId;
        private List<ProductOrderSelectSkuReq> skuList;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ProductOrderSelectSkuReq {
        private String id;
        private Double purchasePrice;
        private Integer purchaseQuantity;
    }

    @SuperBuilder
    @Getter
    public static class ProductOrderQueryRes extends PageRes {
        private List<ProductOrderRes> list;
        @Default
        private List<Map<String, Object>> sources = SourceEnum.TOTALS;
        @Default
        private List<Map<String, Object>> logisticsStatuss = LogisticsStatusEnum.TOTALS;
    }

    @Getter
    @Builder
    public static class ProductOrderDetailRes {
        private String id;
        private String productPic;
        private String productName;
        private String specValues;
        private String subOrderId;
        private Double price;
        private Integer num;
        private Integer logisticsStatus;

        public String getLogisticsStatusStr() {
            return LogisticsStatusEnum.of(logisticsStatus).getLabel();
        }
    }
}