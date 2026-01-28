package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("menu")
public class Menu {
    @TableId(type = IdType.AUTO)
        // ID
        private Integer id;
        // 菜单名称
        private String name;
        // 父级菜单ID
        private Integer parentId;
        // 跳转地址
        private String url;
        // 操作人
        private Integer operatorId;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
