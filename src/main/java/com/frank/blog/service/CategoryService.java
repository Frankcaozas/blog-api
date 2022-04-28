package com.frank.blog.service;

import com.frank.blog.vo.CategoryVo;
import com.frank.blog.vo.Result;

public interface CategoryService {

    CategoryVo getCategoryById(Long categoryId);

    Result getAll();

    Result getAllDetail();
}
