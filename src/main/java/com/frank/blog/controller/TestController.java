package com.frank.blog.controller;

import com.frank.blog.dao.pojo.SysUser;
import com.frank.blog.utils.UserThreadLocal;
import com.frank.blog.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @RequestMapping
    public Result test(){
        SysUser sysUser = UserThreadLocal.get();
        System.out.println(sysUser);
        return Result.success(null);
    }
}