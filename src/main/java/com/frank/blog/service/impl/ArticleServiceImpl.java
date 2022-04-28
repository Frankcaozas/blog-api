package com.frank.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frank.blog.dao.dos.Archives;
import com.frank.blog.dao.mapper.ArticleBodyMapper;
import com.frank.blog.dao.mapper.ArticleMapper;
import com.frank.blog.dao.mapper.ArticleTagMapper;
import com.frank.blog.dao.pojo.Article;
import com.frank.blog.dao.pojo.ArticleBody;
import com.frank.blog.dao.pojo.ArticleTag;
import com.frank.blog.dao.pojo.SysUser;
import com.frank.blog.service.*;
import com.frank.blog.utils.UserThreadLocal;
import com.frank.blog.vo.*;
import com.frank.blog.vo.param.ArticleParam;
import com.frank.blog.vo.param.PageParam;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleBodyMapper articleBodyMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ThreadService threadService;
    @Resource
    private ArticleTagMapper articleTagMapper;


//    @Override
//    public Result articleList(PageParam pageParam) {
//        Page<Article> page = new Page<>(pageParam.getPage(),pageParam.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//        Long categoryId = pageParam.getCategoryId();
//        Long tagId = pageParam.getTagId();
//        //根据分类查询
//        if(categoryId!=null){
//            queryWrapper.eq(Article::getCategoryId,categoryId);
//        }
//        //根据标签查询
//        List<Long> list = new ArrayList<>();
//        if(tagId!=null){
//            LambdaQueryWrapper<ArticleTag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            tagLambdaQueryWrapper.eq(ArticleTag::getTagId,tagId);
//            List<ArticleTag> articleTags = articleTagMapper.selectList(tagLambdaQueryWrapper);
//            for (ArticleTag articleTag:articleTags){
//                list.add(articleTag.getArticleId());
//            }
//            if(!list.isEmpty()){
//                queryWrapper.in(Article::getId,list);
//            }
//        }
    @Cacheable(value = "articleList")
    @Override
    public Result articleList(PageParam pageParam) {
        Page<Article> page = new Page<>(pageParam.getPage(),pageParam.getPageSize());
        IPage<Article> article = articleMapper.listArticle(page, pageParam.getCategoryId(), pageParam.getTagId(), pageParam.getYear(), pageParam.getMonth());
        return Result.success(copyList(article.getRecords(),true,true));
    }


    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,true,true));

    }

    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archives = articleMapper.listArchives();
        return Result.success(archives);
    }

    @Override
    public Result findArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        ArticleVo articleVo = copy(article, true, true, true, true);
        threadService.incViewCount(article);
        return Result.success(articleVo);
    }

    @CacheEvict(value = "articleList")
    @Override
    public Result publish(ArticleParam articleParam) {
        SysUser sysUser = UserThreadLocal.get();
        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setCategoryId(articleParam.getCategory().getId());
        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setViewCounts(0);
        article.setWeight(Article.Article_Common);
        this.articleMapper.insert(article);
        List<TagVo> tags = articleParam.getTags();
        //tags
        if (tags != null) {
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tag.getId());
                this.articleTagMapper.insert(articleTag);
            }
        }
        //body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);

        //更新article的bodyId
        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        HashMap<String, String> map = new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }

    private List<ArticleVo> copyList(List<Article> records,boolean hasTag,boolean hasAuthor) {
        ArrayList<ArticleVo> articleVos = new ArrayList<>();
        for(Article record : records){
            ArticleVo copy = copy(record,hasTag,hasAuthor,false,false);
            articleVos.add(copy);
        }
        return articleVos;
    }

    private ArticleVo copy(Article article, boolean hasTag, boolean hasAuthor, boolean hasBody, boolean hasCategory){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        if(hasTag){
            List<TagVo> tagVos = tagService.getTagVosByArticleId(article.getId());
            articleVo.setTags(tagVos);
        }
        if(hasAuthor){
            SysUser user = sysUserService.getUserById(article.getAuthorId());
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            articleVo.setAuthor(userVo);
        }
        if(hasCategory){
            CategoryVo categoryVo = categoryService.getCategoryById(article.getCategoryId());
            articleVo.setCategory(categoryVo);
        }
        if(hasBody){
            ArticleBodyVo articleBodyVo = getArticleBodyById(article.getBodyId());
            articleVo.setBody(articleBodyVo);
        }
        return articleVo;
    }

    private ArticleBodyVo getArticleBodyById(Long id){
        ArticleBody articleBody = articleBodyMapper.selectById(id);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }
}
