package com.frank.blog.handler;

import com.alibaba.fastjson.JSON;
import com.frank.blog.dao.pojo.SysUser;
import com.frank.blog.service.SysUserService;
import com.frank.blog.utils.UserThreadLocal;
import com.frank.blog.vo.ErrorCode;
import com.frank.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private SysUserService sysUserService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 1.判断 请求路径是否为HandlerMethod (controller方法)
         * 2.判断token是否存在， 为空未登录
         * 3.token不为空 service checkToken
         * 4.认证成功 放行
         */
        if(! (handler instanceof HandlerMethod)){
            return true;
        }
        String token = request.getHeader("Authorization");

        if(token == null || token.equals("undefined")){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        SysUser sysUser = sysUserService.checkToken(token);
        if( sysUser == null ){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        UserThreadLocal.put(sysUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}
