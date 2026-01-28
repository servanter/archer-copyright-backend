package com.archer.admin.web.category.controller;

import com.archer.admin.base.entities.Category;
import com.archer.admin.web.common.SelectOptionLabel;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.category.entities.CategoryTransform.CategoryQueryReq;
import com.archer.admin.web.category.entities.CategoryTransform.CategoryQueryRes;
import com.archer.admin.web.category.entities.CategoryTransform.CategoryRes;
import com.archer.admin.web.category.service.BizCategoryService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.WebContext;

@RequestMapping("/category")
@RestController
@ResponseResultBody
public class CategoryController {

    @Resource
    private BizCategoryService bizCategoryService;

    @RequestMapping("/detail/{categoryId}")
    public CategoryRes detail(WebContext webContext, @PathVariable("categoryId") int categoryId) {
        return bizCategoryService.query(categoryId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody Category category) {
        return bizCategoryService.modify(category);
    }

    @RequestMapping("/list")
    public CategoryQueryRes list(WebContext webContext, CategoryQueryReq categoryQueryReq) {
        return bizCategoryService.list(categoryQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, int id) {
        return bizCategoryService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody Category category) {
        return bizCategoryService.save(category);
    }

    @RequestMapping("/children")
    public List<SelectOptionLabel> list(WebContext webContext, @RequestParam("categoryId") int categoryId) {
        return bizCategoryService.queryChildren(categoryId);
    }

    @RequestMapping("/tree")
    public List<SelectOptionLabel> tree(WebContext webContext) {
        return bizCategoryService.queryCategoryTree();
    }

}
