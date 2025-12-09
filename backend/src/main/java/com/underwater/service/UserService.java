package com.underwater.service;

import com.underwater.entity.User;

public interface UserService {
    /**
     * 用户注册（适配sys_user表）
     */
    boolean register(User user, String code);

    /**
     * 根据邮箱+状态查询用户
     */
    User getByEmailAndStatus(String email, Integer status);

    /**
     * 根据ID查询用户
     */
    User getById(Long id);

    /**
     * 根据用户名查询用户（校验唯一性）
     */
    User getByUsername(String username);

    /**
     * 根据邮箱查询用户（校验唯一性）
     */
    User getByEmail(String email);
}