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
        private int id;
        // 类目名称
        private String categoryName;
        // 一级类目ID
        private int topCategoryId;
        // 二级类目ID
        private int secondCategoryId;
        // 三级类目ID
        private int thirdCategoryId;
        // 状态
        private int status;
        // 状态
        private int valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
