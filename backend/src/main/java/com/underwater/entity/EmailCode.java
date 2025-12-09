package com.underwater.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailCode {
    private Long id;             // ID
    private String email;        // 邮箱
    private String code;         // 6位验证码
    private Integer type;        // 1-注册 2-找回密码
    private LocalDateTime expireTime; // 过期时间
    private Integer used;        // 0-未使用 1-已使用
    private LocalDateTime createTime; // 创建时间
}