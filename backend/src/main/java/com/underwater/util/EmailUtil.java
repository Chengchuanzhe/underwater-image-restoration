package com.underwater.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class EmailUtil {
    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from; // 发件人邮箱

    // 生成6位随机验证码
    public String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 发送验证码邮件
    public boolean sendCodeEmail(String to, String code, int expireMinutes) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("水下图像修复系统 - 验证码");
            message.setText(String.format("你的验证码是：%s，有效期%d分钟，请及时使用！", code, expireMinutes));
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}