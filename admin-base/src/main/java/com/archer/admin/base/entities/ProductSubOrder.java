package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("product_sub_order")
public class ProductSubOrder {
    @TableId(type = IdType.AUTO)
        // ID
        private String id;
        // 订单ID
        private String orderId;
        // 商品ID
        private String productId;
        // SKU ID
        private String skuId;
        // 数量
        private Integer number;
        // 价格
        private Double price;
        // 物流状态
        private Integer logisticsStatus;
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
        // 物流公司
        private String shippingCompany;
        // 物流单号
        private String trackingNumber;
        // 操作人
        private Integer operatorId;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
