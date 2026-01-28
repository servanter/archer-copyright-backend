package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("copyright")
public class Copyright {
    @TableId(type = IdType.AUTO)
        // ID
        private Integer id;
        // IP名称
        private String copyrightName;
        // 海报
        private String placardUrl;
        // 授权方
        private String cpName;
        
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
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
