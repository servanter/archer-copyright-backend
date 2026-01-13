package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("role_menu")
public class RoleMenu {
    @TableId(type = IdType.AUTO)
        // ID
        private Integer id;
        // 角色ID
        private Integer roleId;
        // 菜单ID
        private Integer menuId;
        // 操作人
        private Integer operatorId;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
