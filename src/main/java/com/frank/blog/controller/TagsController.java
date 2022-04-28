package com.frank.blog.controller;


import com.frank.blog.service.TagService;
import com.frank.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
public class TagsController {
    @Autowired
    private TagService tagService;

    @GetMapping("/hot")
    public Result hot(){
        int limit = 6;
        Result hotTags = tagService.getHotTags(6);
        return hotTags;
    }

    @GetMapping
    public Result tagList(){
        return tagService.getAll();
    }

    @GetMapping("detail")
    public Result tagListDetail(){
        return tagService.getAllDetail();
    }

    @GetMapping("detail/{id}")
    public Result tagListDetail(@PathVariable("id") Long id){
        return tagService.getTagById(id);
    }
}
