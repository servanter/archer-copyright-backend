package com.archer.admin.web.copyright.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.Copyright;
import com.archer.admin.web.common.ValidEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
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
        // 海报

        private String placardUrl;
        // 授权方

        private String cpName;
        // 授权到期日期
@JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate authExpireDate;
        // 授权类目

        private String categoryIds;
        // 预留清货天数

        private Integer clearDays;
        // 授权书

        private String authUrl;
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
.placardUrl(copyright.getPlacardUrl())
.cpName(copyright.getCpName())
.authExpireDate(copyright.getAuthExpireDate())
.categoryIds(copyright.getCategoryIds())
.clearDays(copyright.getClearDays())
.authUrl(copyright.getAuthUrl())
.valid(copyright.getValid())
.createTime(copyright.getCreateTime())
.updateTime(copyright.getUpdateTime())
        .build();

    }
public String getValidStr() { 
 return ValidEnum.of(valid).getLabel(); 
}
    }

    @Data
public static class CopyrightQueryReq extends PageReq {
            
        private String copyrightName;
            
        private String cpName;
    }

    @SuperBuilder
@Getter
public static class CopyrightQueryRes extends PageRes {
    private List<CopyrightRes> list;
  
  } 
}