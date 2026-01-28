package com.archer.admin.web.news.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.News;
import com.archer.admin.web.common.ValidEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

public class NewsTransform {
 
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NewsRes {
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
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

    public static NewsRes parseRes(News news) {
return NewsRes.builder()
        .id(news.getId())
.title(news.getTitle())
.content(news.getContent())
.operatorId(news.getOperatorId())
.valid(news.getValid())
.createTime(news.getCreateTime())
.updateTime(news.getUpdateTime())
        .build();

    }
public String getValidStr() { 
 return ValidEnum.of(valid).getLabel(); 
}
    }

    @Data
public static class NewsQueryReq extends PageReq {
            
        private String title;
            
        private String content;
            
        private int operatorId;
            
        private int valid;
    }

    @SuperBuilder
@Getter
public static class NewsQueryRes extends PageRes {
    private List<NewsRes> list;
  
  } 
}