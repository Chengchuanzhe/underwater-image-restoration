package com.underwater.service;

public interface EmailService {
    // 发送注册验证码
    boolean sendRegisterCode(String email);
    // 验证验证码
    boolean verifyRegisterCode(String email, String code);
}