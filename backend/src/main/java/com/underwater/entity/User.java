package com.underwater.entity;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class User {
    private Long id;             // 主键ID
    private String username;     // 用户名（唯一、非空）
    private String password;     // 密码（BCrypt加密、非空）
    private String email;        // 邮箱（唯一、非空）
    private Integer emailVerified; // 邮箱验证状态：0-未验证，1-已验证（默认0）
    private String avatar;       // 头像URL（默认空字符串）
    private LocalDateTime createTime; // 创建时间（自动生成）
    private LocalDateTime updateTime; // 更新时间（自动更新）
    private Integer status;      // 账号状态：1-正常，0-禁用（默认1）
    private String code;         // 临时字段：接收前端验证码（不入库）
}