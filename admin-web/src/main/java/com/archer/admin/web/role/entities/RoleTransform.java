package com.archer.admin.web.role.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.Role;
import com.archer.admin.web.common.ValidEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

public class RoleTransform {
 
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoleRes {
        
        private Integer id;
        // 角色名称

        private String name;
        // 操作人

        private Integer operatorId;
        // 是否可用。1可用；-1失效

        private Integer valid;
        // 创建时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

    public static RoleRes parseRes(Role role) {
return RoleRes.builder()
        .id(role.getId())
.name(role.getName())
.operatorId(role.getOperatorId())
.valid(role.getValid())
.createTime(role.getCreateTime())
.updateTime(role.getUpdateTime())
        .build();

    }
public String getValidStr() { 
 return ValidEnum.of(valid).getLabel(); 
}
    }

    @Data
public static class RoleQueryReq extends PageReq {
            
        private String name;
            
        private int operatorId;
            
        private int valid;
    }

    @SuperBuilder
@Getter
public static class RoleQueryRes extends PageRes {
    private List<RoleRes> list;
    @Default
    private List<Map<String, Object>> valids = ValidEnum.TOTALS;
  
  } 
}