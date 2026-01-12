package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
        
        private Integer id;
        // 用户名
        private String userName;
        // 密码
        private String password;
        // 是否可用。1可用；-1失效
        private Integer valid;
        // 用户类型。1普通用户；2管理员
        private Integer type;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
