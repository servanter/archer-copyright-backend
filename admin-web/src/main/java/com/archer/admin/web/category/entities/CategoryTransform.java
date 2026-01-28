package com.archer.admin.web.category.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.Category;
import com.archer.admin.web.common.ValidEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

public class CategoryTransform {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CategoryRes {
        // ID

        private Integer id;
        // 类目名称

        private String categoryName;
        // 一级类目ID

        private Integer topCategoryId;

        private String topCategoryName;
        // 二级类目ID

        private Integer secondCategoryId;

        private String secondCategoryName;
        // 三级类目ID

        private Integer thirdCategoryId;
        // 状态

        private Integer status;
        // 状态

        private Integer valid;
        // 创建时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

        private int childNum;

        public static CategoryRes parseRes(Category category) {
            return CategoryRes.builder()
                    .id(category.getId())
                    .categoryName(category.getCategoryName())
                    .topCategoryId(category.getTopCategoryId())
                    .secondCategoryId(category.getSecondCategoryId())
                    .thirdCategoryId(category.getThirdCategoryId())
                    .status(category.getStatus())
                    .valid(category.getValid())
                    .createTime(category.getCreateTime())
                    .updateTime(category.getUpdateTime())
                    .build();

        }

        public static CategoryRes parseRes(Category category, Map<Integer, Category> allCategories, Map<Integer, List<Category>> groupCategories) {
            Category topCategory = allCategories.get(category.getTopCategoryId());
            Category secondCategory = allCategories.get(category.getSecondCategoryId());
            int childNum = Optional.ofNullable(groupCategories.get(category.getId())).map(cates->cates.size()).orElse(0);
            return CategoryRes.builder()
                    .id(category.getId())
                    .categoryName(category.getCategoryName())
                    .topCategoryId(category.getTopCategoryId())
                    .topCategoryName(Optional.ofNullable(topCategory).map(Category::getCategoryName).orElse("无"))
                    .secondCategoryId(category.getSecondCategoryId())
                    .secondCategoryName(Optional.ofNullable(secondCategory).map(Category::getCategoryName).orElse("无"))
                    .thirdCategoryId(category.getThirdCategoryId())
                    .status(category.getStatus())
                    .valid(category.getValid())
                    .createTime(category.getCreateTime())
                    .updateTime(category.getUpdateTime())
                    .childNum(childNum)
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
    public static class CategoryQueryReq extends PageReq {

        private String categoryName;

        private int status;

        private int topCategoryId;

        private int secondCategoryId;
    }

    @SuperBuilder
    @Getter
    public static class CategoryQueryRes extends PageRes {

        private List<CategoryRes> list;
        @Default
        private List<Map<String, Object>> statuss = StatusEnum.TOTALS;
    }

}