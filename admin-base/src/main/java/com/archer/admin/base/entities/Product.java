package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
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
        
@JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate shippingDate;
        // 销售渠道
        private String saleChannelIds;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
