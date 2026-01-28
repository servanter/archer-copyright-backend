package com.archer.admin.web.productpic.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.ProductPic;
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

public class ProductPicTransform {
 
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProductPicRes {
        // ID

        private Integer id;
        // 活动ID

        private String productId;
        // 文件类型

        private Integer fileType;
        // 文件名

        private String fileName;
        // 文件下载地址

        private String fileUrl;
        // 状态

        private Integer valid;
        // 创建时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

    public static ProductPicRes parseRes(ProductPic productpic) {
return ProductPicRes.builder()
        .id(productpic.getId())
.productId(productpic.getProductId())
.fileType(productpic.getFileType())
.fileName(productpic.getFileName())
.fileUrl(productpic.getFileUrl())
.valid(productpic.getValid())
.createTime(productpic.getCreateTime())
.updateTime(productpic.getUpdateTime())
        .build();

    }
public String getFileTypeStr() { 
 return FileTypeEnum.of(fileType).getLabel(); 
}

public String getValidStr() { 
 return ValidEnum.of(valid).getLabel(); 
}
    }

    @Data
public static class ProductPicQueryReq extends PageReq {
            
        private String productId;
            
        private int fileType;
            
        private String fileName;
            
        private String fileUrl;
    }

    @SuperBuilder
@Getter
public static class ProductPicQueryRes extends PageRes {
    private List<ProductPicRes> list;
  @Default
private List<Map<String, Object>> fileTypes = FileTypeEnum.TOTALS;
  } 
}