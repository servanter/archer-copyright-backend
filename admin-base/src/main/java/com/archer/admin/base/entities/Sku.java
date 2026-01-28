package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("sku")
public class Sku {
    @TableId(type = IdType.AUTO)
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
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
