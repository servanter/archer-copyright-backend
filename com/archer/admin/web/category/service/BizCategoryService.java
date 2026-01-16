package com.archer.admin.web.category.service;

import com.archer.admin.base.entities.Category;
import com.archer.admin.base.service.CategoryService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.category.entities.CategoryTransform.CategoryQueryReq;
import com.archer.admin.web.category.entities.CategoryTransform.CategoryQueryRes;
import com.archer.admin.web.category.entities.CategoryTransform.CategoryRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
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
        Page<Category> page = categoryService.lambdaQuery()
                
.like(StringUtils.isNotBlank(categoryQueryReq.getCategoryName()), Category::getCategoryName, categoryQueryReq.getCategoryName())



.eq(categoryQueryReq.getStatus() != 0, Category::getStatus, categoryQueryReq.getStatus())


                .page(new Page<>(categoryQueryReq.getPageNo(), categoryQueryReq.getPageSize()));

        List<CategoryRes> list = page.getRecords().stream().map(CategoryRes::parseRes).collect(Collectors.toList());
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
        return categoryService.save(category) ? Result.success() : Result.error();
    }
}
