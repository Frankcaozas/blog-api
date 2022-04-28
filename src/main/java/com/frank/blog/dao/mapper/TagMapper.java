package com.frank.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frank.blog.dao.pojo.Tag;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章id查询对应的标签
     * @param articleId
     * @return
     */
    List<Tag> getTagsByArticleId(Long articleId);

    List<Long> getHotTagIds(int limit);
}
