package com.archer.admin.web.user.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.User;
import com.archer.admin.web.common.ValidEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class UserTransform {
 
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserRes {
        // ID

        private Integer id;
        // 用户名

        private String userName;
        // 密码

        private String password;
        // 状态

        private Integer valid;
        // 用户类型

        private Integer type;
        // 创建时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

    public static UserRes parseRes(User user) {
return UserRes.builder()
        .id(user.getId())
.userName(user.getUserName())
.password(user.getPassword())
.valid(user.getValid())
.type(user.getType())
.createTime(user.getCreateTime())
.updateTime(user.getUpdateTime())
        .build();

    }
public String getValidStr() { 
 return ValidEnum.of(valid).getLabel(); 
}

public String getTypeStr() { 
 return TypeEnum.of(type).getLabel(); 
}
    }

    @Data
public static class UserQueryReq extends PageReq {
            
        private String userName;
            
        private String password;
            
        private int valid;
            
        private int type;
    }

    @SuperBuilder
@Getter
public static class UserQueryRes extends PageRes {
    private List<UserRes> list;
  @Default
private List<Map<String, Object>> types = TypeEnum.TOTALS;
  } 
}