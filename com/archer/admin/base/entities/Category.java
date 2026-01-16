package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
        // ID
        private Integer id;
        // 类目名称
        private String categoryName;
        // 一级类目ID
        private Integer topCategoryId;
        // 二级类目ID
        private Integer secondCategoryId;
        // 三级类目ID
        private Integer thirdCategoryId;
        // 状态
        private Integer status;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
