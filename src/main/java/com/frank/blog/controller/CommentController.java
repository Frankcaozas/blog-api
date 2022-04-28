package com.frank.blog.controller;

import com.frank.blog.service.CommentService;
import com.frank.blog.vo.Result;

import com.frank.blog.vo.param.CommentParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Resource
    private CommentService commentService;

    @GetMapping("article/{id}")
    public Result comments(@PathVariable("id") Long articleId){

        return commentService.getCommentsByArticleId(articleId);

    }

    @PostMapping("/create/change")
    public Result comment(@RequestBody CommentParam commentParam){
        return commentService.comment(commentParam);
    }
}
