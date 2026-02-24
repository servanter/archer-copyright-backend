package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("stock_change_log")
public class StockChangeLog {
    @TableId(type = IdType.AUTO)
        // ID
        private Integer id;
        // SKU ID
        private String skuId;
        // 产品ID
        private String productId;
        // 库存数量
        private Integer stock;
        // 操作人ID
        private Integer operatorId;
        // 类型
        private Integer type;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
