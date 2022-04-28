package com.frank.blog.service;

import com.frank.blog.dao.pojo.SysUser;
import com.frank.blog.vo.Result;
import com.frank.blog.vo.UserVo;
import com.frank.blog.vo.param.LoginParam;

public interface SysUserService {
    UserVo getUserVoByID(Long id);

    SysUser getUserById(Long id);

    /**
     * 登录
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    /**
     * 根据token在redis查询用户信息
     * @param token
     * @return
     */
    Result getUserInfoByToken(String token);

    /**
     * 退出登录
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 注册
     * @param loginParam
     * @return
     */
    Result register(LoginParam loginParam);

    SysUser findUserByAccount(String account);

    void save(SysUser user);

    SysUser checkToken(String token);
}
