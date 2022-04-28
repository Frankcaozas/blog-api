package com.frank.blog.controller;


import com.frank.blog.dao.pojo.Article;
import com.frank.blog.service.ArticleService;
import com.frank.blog.vo.Result;
import com.frank.blog.vo.param.ArticleParam;
import com.frank.blog.vo.param.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    /**
     * 首页文章列表
     * @param pageParam
     * @return
     */
    @PostMapping
    public Result articleList(@RequestBody PageParam pageParam){
        return articleService.articleList(pageParam);
    }


    @PostMapping("/hot")
    public Result hotArticle(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    @PostMapping("/new")
    public Result newArticle(){
        int limit = 5;
        return articleService.newArticles(limit);
    }

    @PostMapping("listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }

    @PostMapping("/view/{id}")
    public Result articleView(@PathVariable Long id){
        return articleService.findArticleById(id);
    }

    @PostMapping("/publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }

}
