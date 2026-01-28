package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("product_pic")
public class ProductPic {
    @TableId(type = IdType.AUTO)
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
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
