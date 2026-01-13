package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("news")
public class News {
    @TableId(type = IdType.AUTO)
        // ID
        private Integer id;
        // 标题
        private String title;
        // 内容
        private String content;
        // 操作人
        private Integer operatorId;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
