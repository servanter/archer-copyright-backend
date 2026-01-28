package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("product_channel_sku")
public class ProductChannelSku {
    @TableId(type = IdType.AUTO)
        // ID
        private Integer id;
        // 商品ID
        private String productId;
        // 平台商品ID
        private String platformProductId;
// 渠道ID
        private Integer channelId;
        // SKUID
        private String skuId;
        // 锁定数量
        private Integer lockNum;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
