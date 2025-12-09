package com.underwater.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MailConfig {

    // 从application.yml中读取发件人邮箱（和spring.mail.username一致）
    @Value("${spring.mail.username}")
    private String fromEmail;

    // 定义SimpleMailMessage Bean，解决注入失败问题
    @Bean
    public SimpleMailMessage simpleMailMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // 设置发件人（必须和邮箱配置的username一致）
        return message;
    }
}
