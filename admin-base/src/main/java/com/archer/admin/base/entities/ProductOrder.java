package com.archer.admin.base.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("product_order")
public class ProductOrder {
    @TableId(type = IdType.AUTO)
        // ID
        private String id;
        // 总数量
        private Double totalCount;
        // 总价格
        private Double totalPrice;
        // 来源
        private Integer source;
        // 物流状态
        private Integer logisticsStatus;
        // 采购方
        private String purchaser;
        // 采购合同文件
        private String purchaseContractFile;
        // 收件人
        private String receiver;
        // 收件人手机号
        private String receiverPhone;
        // 收件人省份
        private String receiverProvince;
        // 收件人城市
        private String receiverCity;
        // 收件人区域
        private String receiverArea;
        // 收件人地址
        private String receiverAddress;
        // 操作人
        private Integer operatorId;
        // 状态
        private Integer valid;
        // 创建时间
        private LocalDateTime createTime;
        // 修改时间
        private LocalDateTime updateTime;
}
