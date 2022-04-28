package com.frank.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frank.blog.dao.mapper.TagMapper;
import com.frank.blog.dao.pojo.Category;
import com.frank.blog.dao.pojo.Tag;
import com.frank.blog.service.TagService;
import com.frank.blog.vo.Result;
import com.frank.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Resource
    private TagMapper tagMapper;

    @Override
    public List<TagVo> getTagVosByArticleId(Long articleId) {
        List<Tag> tags = tagMapper.getTagsByArticleId(articleId);
        List<TagVo> tagVos = copyList(tags);
        return tagVos;
    }

    @Override
    public Result getHotTags(int limit) {
        //根据tagId分组计数找到文章数量最多的tagId
        List<Long> tagIds = tagMapper.getHotTagIds(limit);
        if (CollectionUtils.isEmpty(tagIds)) {
            return Result.success(Collections.emptyList());
        }
        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        return Result.success(tags);
    }

    @Override
    public Result getAll() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId,Tag::getTagName);
        List<Tag> tagList = tagMapper.selectList(queryWrapper);
        List<TagVo> tagVos = copyList(tagList);
        return Result.success(tagVos);
    }

    @Override
    public Result getAllDetail() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        List<Tag> tags = tagMapper.selectList(queryWrapper);
        return Result.success(copyList(tags));
    }

    @Override
    public Result getTagById(Long id) {
        return Result.success(copy(tagMapper.selectById(id)));
    }

    public TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
    public List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }
}
