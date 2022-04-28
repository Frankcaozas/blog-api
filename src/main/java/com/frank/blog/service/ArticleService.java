package com.frank.blog.service;

import com.frank.blog.vo.Result;
import com.frank.blog.vo.param.ArticleParam;
import com.frank.blog.vo.param.PageParam;


public interface ArticleService {


    Result articleList(PageParam pageParam);

    Result hotArticle(int limit);

    Result newArticles(int limit);

    Result listArchives();

    Result findArticleById(Long id);

    Result publish(ArticleParam articleParam);
}
