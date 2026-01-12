package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("menu")
public class Menu {
    @TableId(type = IdType.AUTO)
        
        private Integer id;
        // 菜单名称
        private String name;
        // 父级菜单ID
        private Integer parentId;
        // 跳转地址
        private String url;
        // 操作人
        private Integer operatorId;
        // 是否可用。1可用；-1失效
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
