package com.frank.blog.service;

import com.frank.blog.vo.Result;
import com.frank.blog.vo.TagVo;

import java.util.List;

public interface TagService {



    /**
     * 根据文章id查询对应的标签
     * @param articleId
     * @return
     */
    List<TagVo> getTagVosByArticleId(Long articleId);

    /**
     * 热度最高的标签
     * @param limit
     * @return
     */
    Result getHotTags(int limit);

    Result getAll();

    Result getAllDetail();

    Result getTagById(Long id);
}
