package com.frank.blog.controller;

import com.frank.blog.service.CategoryService;
import com.frank.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorys")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result listCategory(){
        return categoryService.getAll();
    }

    @GetMapping("detail")
    public Result listCategoryDetail(){
        return categoryService.getAllDetail();
    }

    @GetMapping("detail/{id}")
    public Result listCategoryDetailById(@PathVariable("id") Long id){
        return Result.success(categoryService.getCategoryById(id));
    }


}
