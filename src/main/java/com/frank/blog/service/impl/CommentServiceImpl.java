package com.frank.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frank.blog.aop.LogAnnotation;
import com.frank.blog.dao.mapper.CommentMapper;
import com.frank.blog.dao.pojo.Comment;
import com.frank.blog.dao.pojo.SysUser;
import com.frank.blog.service.CommentService;
import com.frank.blog.service.SysUserService;
import com.frank.blog.service.ThreadService;
import com.frank.blog.utils.UserThreadLocal;
import com.frank.blog.vo.CommentVo;
import com.frank.blog.vo.Result;
import com.frank.blog.vo.UserVo;
import com.frank.blog.vo.param.CommentParam;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ThreadService threadService;
    @Override
    public Result getCommentsByArticleId(Long articleId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,articleId);
        queryWrapper.eq(Comment::getLevel,1);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        return Result.success(copyList(comments));
    }

    @LogAnnotation(module = "comment", operation = "comment")
    @Override
    public Result comment(CommentParam commentParam) {
        SysUser sysUser = UserThreadLocal.get();
        Comment comment = new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setAuthorId(sysUser.getId());
        comment.setContent(commentParam.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParam.getParent();
        if(parent == null || parent == 0){
            comment.setLevel(1);
        }else{
            comment.setLevel(2);
        }
        comment.setParentId(parent == null ? 0 : parent);
        Long toUserId = commentParam.getToUserId();
        comment.setToUid(toUserId == null ? 0 : toUserId);
        this.commentMapper.insert(comment);
        threadService.incCommentCount(commentParam.getArticleId());
        return Result.success(null);
    }

    private List<CommentVo> getCommentsByParentId(Long id){
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        return copyList(comments);
    }

    private List<CommentVo> copyList(List<Comment> comments){
        List<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment:comments){
            commentVos.add(copy(comment));
        }
        return commentVos;
    }

    private CommentVo copy(Comment comment){
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        commentVo.setCreateDate(new DateTime(comment.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //查询作者信息
        UserVo userVo = sysUserService.getUserVoByID(comment.getAuthorId());
        commentVo.setAuthor(userVo);
        Integer level = comment.getLevel();
        if(level == 1){
            List<CommentVo> commentVoList = getCommentsByParentId(comment.getId());
            commentVo.setChildrens(commentVoList);
        }
        if(level > 1){
            UserVo toUserVo = sysUserService.getUserVoByID(comment.getToUid());
            commentVo.setToUser(toUserVo);
        }
        return commentVo;
    }
}
