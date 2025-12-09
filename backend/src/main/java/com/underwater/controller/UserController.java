package com.underwater.controller;

import com.underwater.entity.Result;
import com.underwater.entity.User;
import com.underwater.service.UserService;
import com.underwater.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 用户控制器（适配sys_user表）
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private JwtUtil jwtUtil;

    // 注册接口 - 适配数据库字段
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        String code = user.getCode(); // 临时验证码字段
        boolean success = userService.register(user, code);
        if (success) {
            return Result.success("注册成功，请登录");
        }
        return Result.error("注册失败，用户名/邮箱已存在或验证码错误");
    }

    // 登录接口 - 适配数据库字段
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        String email = user.getEmail();
        String password = user.getPassword();

        // 1. 查询用户（仅查正常状态的用户）
        User dbUser = userService.getByEmailAndStatus(email, 1);
        if (dbUser == null) {
            return Result.error("邮箱未注册或账号已禁用");
        }

        // 2. 校验密码（BCrypt匹配）
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        if (!encoder.matches(password, dbUser.getPassword())) {
            return Result.error("密码错误");
        }

        // 3. 生成JWT Token
        String token = jwtUtil.generateToken(dbUser.getId());

        // 4. 隐藏敏感字段，返回用户信息
        dbUser.setPassword(null);
        return Result.success(new TokenVO(token, dbUser));
    }

    // 获取当前登录用户信息
    @GetMapping("/info")
    public Result<?> getUserInfo(@RequestAttribute Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null); // 隐藏密码
        return Result.success(user);
    }

    // 退出登录
    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.success("退出成功");
    }

    // Token返回VO
    public static class TokenVO {
        private String token;
        private User user;

        public TokenVO(String token, User user) {
            this.token = token;
            this.user = user;
        }

        // Getter & Setter
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
}