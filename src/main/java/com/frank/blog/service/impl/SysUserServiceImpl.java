package com.frank.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frank.blog.dao.mapper.SysUserMapper;
import com.frank.blog.dao.pojo.SysUser;
import com.frank.blog.service.SysUserService;
import com.frank.blog.utils.JWTUtils;
import com.frank.blog.vo.ErrorCode;
import com.frank.blog.vo.LoginUserVo;
import com.frank.blog.vo.Result;
import com.frank.blog.vo.UserVo;
import com.frank.blog.vo.param.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.awt.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class SysUserServiceImpl implements SysUserService {
    private static final String slat = "frankcao!@#";
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public UserVo getUserVoByID(Long id) {
        UserVo userVo = new UserVo();
        SysUser userById = getUserById(id);
        BeanUtils.copyProperties(userById,userVo);
        return userVo;
    }

    @Override
    public SysUser getUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if(sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("frankcao");
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setId(1L);
        }
        return sysUser;
    }

    @Override
    public Result login(LoginParam loginParam) {
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        String pwd = DigestUtils.md5Hex(password + slat);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,pwd);
        queryWrapper.select(SysUser::getId,SysUser::getAccount,SysUser::getAvatar,SysUser::getNickname);
        queryWrapper.last("limit 1");
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        if(sysUser != null){
            String token = JWTUtils.createToken(sysUser.getId());
            redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
            return Result.success(token);
        }
        return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
    }

    @Override
    public Result getUserInfoByToken(String token) {
        SysUser user = checkToken(token);
        if(user == null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(user.getId());
        loginUserVo.setAccount(user.getAccount());
        loginUserVo.setNickname(user.getNickname());
        loginUserVo.setAvatar(user.getAvatar());
        return Result.success(user);
    }

    @Override
    public Result logout(String token) {
        Boolean delete = redisTemplate.delete("TOKEN_" + token);
        if(!delete){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg() );
        }
        return Result.success(null);
    }

    @Override
    public Result register(LoginParam loginParam) {
        //1.验证参数
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if (StringUtils.isAnyBlank(account,password,nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        //2.查询用户是否存在
        SysUser sysUser = findUserByAccount(account);
        if (sysUser != null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(),ErrorCode.ACCOUNT_EXIST.getMsg());
        }
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        save(sysUser);
        //3.生成token
        //token
        String token = JWTUtils.createToken(sysUser.getId());
        //4.放入redis
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrappe = new LambdaQueryWrapper<>();
        queryWrappe.eq(SysUser::getAccount,account);
        queryWrappe.last("limit 1");
        SysUser sysUser = sysUserMapper.selectOne(queryWrappe);
        return sysUser;
    }

    @Override
    public void save(SysUser user) {
        sysUserMapper.insert(user);
    }

    @Override
    public SysUser checkToken(String token){
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> map = JWTUtils.checkToken(token);
        if(map == null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(StringUtils.isBlank(userJson)){
            return null;
        }
        return JSON.parseObject(userJson,SysUser.class);
    }
}
