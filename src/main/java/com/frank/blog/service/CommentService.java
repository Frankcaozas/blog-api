package com.frank.blog.service;

import com.frank.blog.vo.Result;
import com.frank.blog.vo.param.CommentParam;

public interface CommentService {


    Result getCommentsByArticleId(Long articleId);

    Result comment(CommentParam commentParam);
}
