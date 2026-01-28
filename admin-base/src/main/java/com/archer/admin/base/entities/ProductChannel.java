package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("product_channel")
public class ProductChannel {
    @TableId(type = IdType.AUTO)
        // ID
        private Integer id;
        // 商品ID
        private String productId;
        // 平台商品ID
        private String platformProductId;
        // 库存策略
        private Integer stockStrategy;
        // 渠道ID
        private Integer channelId;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
