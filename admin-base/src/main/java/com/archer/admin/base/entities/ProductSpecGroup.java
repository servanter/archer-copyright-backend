package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("product_spec_group")
public class ProductSpecGroup {
        // ID
        private String id;
        // 商品ID
        private String productId;
        // 规格组
        private String specName;
        // 排序
        private Integer sort;
        // 状态
        private Integer valid;
        // 操作人
        private Integer operatorId;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
