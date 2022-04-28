package com.frank.blog.controller;


import com.frank.blog.service.SysUserService;
import com.frank.blog.vo.Result;
import com.frank.blog.vo.param.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginParam loginParam){
        return sysUserService.login(loginParam);
    }

    @GetMapping("/currentUser")
    public Result currentUser(@RequestHeader("Authorization") String token){
        return sysUserService.getUserInfoByToken(token);
    }

    @GetMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String token){
        return sysUserService.logout(token);
    }

    @PostMapping("/register")
    public Result register(@RequestBody LoginParam loginParam){
        return sysUserService.register(loginParam);
    }
}
