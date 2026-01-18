package com.archer.admin.web.copyright.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.Copyright;
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

public class CopyrightTransform {
 
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CopyrightRes {
        // ID

        private Integer id;
        // IP名称

        private String copyrightName;
        // 授权方

        private String cpName;
        // 状态

        private Integer status;
        // 授权到期日期
@JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime authExpireDate;
        // 授权类目

        private Integer topCategoryId;
        // 预留清货天数

        private Integer clearDays;
        // 授权书

        private String authUrl;
        // 海报

        private String placardUrl;
        // 状态

        private Integer valid;
        // 创建时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

    public static CopyrightRes parseRes(Copyright copyright) {
return CopyrightRes.builder()
        .id(copyright.getId())
.copyrightName(copyright.getCopyrightName())
.cpName(copyright.getCpName())
.status(copyright.getStatus())
.authExpireDate(copyright.getAuthExpireDate())
.topCategoryId(copyright.getTopCategoryId())
.clearDays(copyright.getClearDays())
.authUrl(copyright.getAuthUrl())
.placardUrl(copyright.getPlacardUrl())
.valid(copyright.getValid())
.createTime(copyright.getCreateTime())
.updateTime(copyright.getUpdateTime())
        .build();

    }
public String getStatusStr() { 
 return StatusEnum.of(status).getLabel(); 
}

public String getValidStr() { 
 return ValidEnum.of(valid).getLabel(); 
}
    }

    @Data
public static class CopyrightQueryReq extends PageReq {
            
        private String copyrightName;
            
        private String cpName;
            
        private int status;
    }

    @SuperBuilder
@Getter
public static class CopyrightQueryRes extends PageRes {
    private List<CopyrightRes> list;
  @Default
private List<Map<String, Object>> statuss = StatusEnum.TOTALS;
  } 
}