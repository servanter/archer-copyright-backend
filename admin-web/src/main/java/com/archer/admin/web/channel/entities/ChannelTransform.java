package com.archer.admin.web.channel.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.Channel;
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

public class ChannelTransform {
 
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ChannelRes {
        // ID

        private Integer id;
        // 渠道名称

        private String channelName;
        // 状态

        private Integer valid;
        // 创建时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

    public static ChannelRes parseRes(Channel channel) {
return ChannelRes.builder()
        .id(channel.getId())
.channelName(channel.getChannelName())
.valid(channel.getValid())
.createTime(channel.getCreateTime())
.updateTime(channel.getUpdateTime())
        .build();

    }
public String getValidStr() { 
 return ValidEnum.of(valid).getLabel(); 
}
    }

    @Data
public static class ChannelQueryReq extends PageReq {
            
        private String channelName;
    }

    @SuperBuilder
@Getter
public static class ChannelQueryRes extends PageRes {
    private List<ChannelRes> list;
  
  } 
}