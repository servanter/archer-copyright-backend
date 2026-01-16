package com.archer.admin.web.category.service;

import com.archer.admin.base.entities.Category;
import com.archer.admin.base.service.CategoryService;
import com.archer.admin.web.category.entities.CategoryTransform.CategoryQueryReq;
import com.archer.admin.web.category.entities.CategoryTransform.CategoryQueryRes;
import com.archer.admin.web.category.entities.CategoryTransform.CategoryRes;
import com.archer.admin.web.common.SelectOptionLabel;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.util.Utils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizCategoryService {

    @Resource
    private CategoryService categoryService;

    public CategoryRes query(int categoryId) {
        return CategoryRes.parseRes(categoryService.getById(categoryId));
    }

    public Result modify(Category category) {
        return categoryService.updateById(category) ? Result.success() : Result.error();
    }

    public CategoryQueryRes list(CategoryQueryReq categoryQueryReq) {
        Page<Category> page;
        if(categoryQueryReq.getTopCategoryId() <= 0) {

            // 只要一级分类
            page = categoryService.lambdaQuery()

                    .like(StringUtils.isNotBlank(categoryQueryReq.getCategoryName()), Category::getCategoryName,
                            categoryQueryReq.getCategoryName())

                    .eq(categoryQueryReq.getStatus() != 0, Category::getStatus, categoryQueryReq.getStatus())

                    .eq(Category::getTopCategoryId, -1)

                    .page(new Page<>(categoryQueryReq.getPageNo(), categoryQueryReq.getPageSize()));
        } else if (categoryQueryReq.getTopCategoryId() > 0 && categoryQueryReq.getSecondCategoryId() <= 0){
            page = categoryService.lambdaQuery()

                    .like(StringUtils.isNotBlank(categoryQueryReq.getCategoryName()), Category::getCategoryName,
                            categoryQueryReq.getCategoryName())

                    .eq(categoryQueryReq.getStatus() != 0, Category::getStatus, categoryQueryReq.getStatus())

                    .eq(Category::getTopCategoryId, categoryQueryReq.getTopCategoryId())

                    .le(Category::getSecondCategoryId, 0)

                    .page(new Page<>(categoryQueryReq.getPageNo(), categoryQueryReq.getPageSize()));
        } else {
            page = categoryService.lambdaQuery()

                    .like(StringUtils.isNotBlank(categoryQueryReq.getCategoryName()), Category::getCategoryName,
                            categoryQueryReq.getCategoryName())

                    .eq(categoryQueryReq.getStatus() != 0, Category::getStatus, categoryQueryReq.getStatus())

                    .eq(Category::getSecondCategoryId, categoryQueryReq.getSecondCategoryId())

                    .page(new Page<>(categoryQueryReq.getPageNo(), categoryQueryReq.getPageSize()));
        }

        List<Category> all = categoryService.lambdaQuery().list();
        Map<Integer, Category> allCategories = all.stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        Map<Integer, List<Category>> topScopeSecondCates = all.stream()
                .filter(category -> category.getTopCategoryId() > 0 && category.getSecondCategoryId() <= 0)
                .collect(Collectors.groupingBy(Category::getTopCategoryId));

        Map<Integer, List<Category>> secondScopeThirdCates = all.stream()
                .filter(category -> category.getTopCategoryId() > 0 && category.getSecondCategoryId() > 0)
                .collect(Collectors.groupingBy(Category::getSecondCategoryId));


        Map<Integer, List<Category>> groupCategories = Maps.newHashMapWithExpectedSize(10);
        groupCategories.putAll(topScopeSecondCates);
        groupCategories.putAll(secondScopeThirdCates);


        List<CategoryRes> list = page.getRecords().stream().map(category -> CategoryRes.parseRes(category, allCategories, groupCategories)).collect(Collectors.toList());
        return CategoryQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(int categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        category.setValid(-1);
        return categoryService.updateById(category) ? Result.success() : Result.error();
    }

    public Result save(Category category) {
        // 一级分类区间 10000 ~ 100000
        // 二级级分类区间 200000 ~ 2000000
        // 三级级分类区间 3000000 ~ 30000000
        if (Utils.isMoreThanZero(category.getTopCategoryId())) {
            if (Utils.isMoreThanZero(category.getSecondCategoryId())) {
                // 新增三级分类
                int nextCategoryId = getNextCategoryId(CategoryLevel.LEVEL_3);
                category.setId(nextCategoryId);
            } else {
                // 新增二级分类
                int nextCategoryId = getNextCategoryId(CategoryLevel.LEVEL_2);
                category.setId(nextCategoryId);
            }
        } else {
            // 新增一级分类
            int nextCategoryId = getNextCategoryId(CategoryLevel.LEVEL_1);
            category.setId(nextCategoryId);
        }
        return categoryService.save(category) ? Result.success() : Result.error();
    }

    private int getNextCategoryId(CategoryLevel categoryLevel) {
        if (categoryLevel == CategoryLevel.LEVEL_1) {
            return categoryService.lambdaQuery()
                    .eq(Category::getTopCategoryId, -1)
                    .select(Category::getId)
                    .orderByDesc(Category::getId)
                    .last(" LIMIT 1")
                    .oneOpt().map(Category::getId)
                    .orElse(10000) + 1;
        } else if (categoryLevel == CategoryLevel.LEVEL_2) {
            return categoryService.lambdaQuery()
                    .between(Category::getId, 200000, 2000000)
                    .select(Category::getId)
                    .orderByDesc(Category::getId)
                    .last(" LIMIT 1")
                    .oneOpt().map(Category::getId)
                    .orElse(200000) + 1;
        } else {
            return categoryService.lambdaQuery()
                    .between(Category::getId, 3000000, 30000000)
                    .select(Category::getId)
                    .orderByDesc(Category::getId)
                    .last(" LIMIT 1")
                    .oneOpt().map(Category::getId)
                    .orElse(3000000) + 1;
        }
    }

    public List<SelectOptionLabel> queryChildren(int categoryId) {
        CategoryLevel categoryLevel = CategoryLevel.getSearchCategory(categoryId);

        switch (categoryLevel) {
            case LEVEL_1:
                return categoryService.lambdaQuery()
                        .eq(Category::getTopCategoryId, categoryId)
                        .list()
                        .stream()
                        .map(category -> SelectOptionLabel.of(category.getCategoryName(), category.getId()))
                        .collect(Collectors.toList());
            case LEVEL_2:
                return categoryService.lambdaQuery()
                        .eq(Category::getTopCategoryId, categoryId)
                        .eq(Category::getSecondCategoryId, -1)
                        .list()
                        .stream()
                        .map(category -> SelectOptionLabel.of(category.getCategoryName(), category.getId()))
                        .collect(Collectors.toList());
            default:
                return categoryService.lambdaQuery()
                        .eq(Category::getSecondCategoryId, categoryId)
                        .list()
                        .stream()
                        .map(category -> SelectOptionLabel.of(category.getCategoryName(), category.getId()))
                        .collect(Collectors.toList());
        }
    }

    public static enum CategoryLevel {
        LEVEL_1,
        LEVEL_2,
        LEVEL_3;

        public static CategoryLevel getSearchCategory(int categoryId) {
            if (categoryId < 10000) {
                return LEVEL_1;
            }
            if (categoryId < 200000) {
                return LEVEL_2;
            }
            return LEVEL_3;
        }
    }
}
