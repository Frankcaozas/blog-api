package com.frank.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.frank.blog.dao.mapper.ArticleMapper;
import com.frank.blog.dao.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ThreadService {
    @Resource
    private ArticleMapper articleMapper;

    @Async("taskExecutor")
    public void incViewCount(Article article){
        Article updateArticle = new Article();
        updateArticle.setViewCounts(article.getViewCounts()+1);
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,article.getId());
        //确认没有被其他线程修改
        updateWrapper.eq(Article::getViewCounts,article.getViewCounts());

        //测试
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        articleMapper.update(updateArticle,updateWrapper);
    }
    @Async("taskExecutor")
    public void incCommentCount(Long id){
        Article updateArticle = new Article();
        log.info("进入");
        Article article = articleMapper.selectById(id);
        updateArticle.setCommentCounts(article.getCommentCounts()+1);
        LambdaUpdateWrapper<Article> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(Article::getId,article.getId());
        queryWrapper.eq(Article::getCommentCounts,article.getCommentCounts());
        articleMapper.update(updateArticle, queryWrapper);
    }


}
