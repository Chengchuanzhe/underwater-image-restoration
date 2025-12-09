package com.underwater.controller;

import com.underwater.entity.Result;
import com.underwater.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Resource
    private EmailService emailService;

    // 发送注册验证码
    @GetMapping("/code")
    public Result<?> sendRegisterCode(@RequestParam String email) {
        boolean success = emailService.sendRegisterCode(email);
        if (success) {
            return Result.success("验证码已发送，请查收");
        }
        return Result.error("验证码发送失败，请重试");
    }
}