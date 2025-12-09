package com.underwater.service.impl;

import com.underwater.entity.User;
import com.underwater.mapper.UserMapper;
import com.underwater.service.EmailService;
import com.underwater.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 用户服务实现（适配sys_user表）
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private EmailService emailService;

    @Override
    public boolean register(User user, String code) {
        // 1. 校验验证码
        boolean codeValid = emailService.verifyRegisterCode(user.getEmail(), code);
        if (!codeValid) {
            return false;
        }

        // 2. 校验用户名/邮箱是否已存在（唯一约束）
        if (getByUsername(user.getUsername()) != null) {
            return false;
        }
        if (getByEmail(user.getEmail()) != null) {
            return false;
        }

        // 3. 设置默认值（匹配sys_user表默认值）
        user.setEmailVerified(0); // 邮箱未验证
        user.setAvatar("");       // 头像默认空字符串
        user.setStatus(1);        // 账号默认正常
        // createTime/updateTime由数据库自动生成，无需手动设置

        // 4. BCrypt加密密码（匹配表中password字段存储规则）
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));

        // 5. 插入数据库
        return userMapper.insert(user) > 0;
    }

    @Override
    public User getByEmailAndStatus(String email, Integer status) {
        return userMapper.selectByEmailAndStatus(email, status);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.selectByEmail(email);
    }
}