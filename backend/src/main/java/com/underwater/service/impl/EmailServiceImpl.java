package com.underwater.service.impl;

import com.underwater.entity.EmailCode;
import com.underwater.mapper.EmailCodeMapper;
import com.underwater.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {
    @Resource
    private EmailCodeMapper emailCodeMapper;
    @Resource
    private JavaMailSender mailSender; // Spring邮件发送器
    @Resource
    private SimpleMailMessage simpleMailMessage; // 简化邮件对象

    // 生成6位随机验证码
    private String generateCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(899999) + 100000); // 100000-999999
    }

    // 真实发送注册验证码到邮箱
    @Override
    public boolean sendRegisterCode(String email) {
        try {
            // 1. 生成6位验证码
            String code = generateCode();
            // 2. 构建验证码实体（存入数据库）
            EmailCode emailCode = new EmailCode();
            emailCode.setEmail(email);
            emailCode.setCode(code);
            emailCode.setType(1); // 1=注册
            emailCode.setExpireTime(LocalDateTime.now().plusMinutes(15)); // 15分钟过期
            emailCode.setUsed(0); // 0=未使用

            // 3. 先删除该邮箱已存在的未使用验证码（避免重复）
            emailCodeMapper.deleteUnusedByEmail(email);
            // 4. 插入新验证码
            emailCodeMapper.insert(emailCode);

            // 5. 组装邮件并发送
            simpleMailMessage.setTo(email); // 收件人邮箱
            simpleMailMessage.setSubject("水下图片修复系统 - 注册验证码"); // 邮件标题
            simpleMailMessage.setText(String.format(
                    "您好！您的注册验证码是：%s\n该验证码15分钟内有效，请及时完成注册。\n如非本人操作，请忽略此邮件。",
                    code
            )); // 邮件内容
            mailSender.send(simpleMailMessage); // 发送邮件

            System.out.println("✅ 验证码已发送到邮箱：" + email + "，验证码：" + code);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常（方便排查）
            System.out.println("❌ 发送失败：" + e.getMessage());
            return false;
        }
    }

    // 验证注册验证码
    @Override
    public boolean verifyRegisterCode(String email, String code) {
        // 1. 查询该邮箱未使用且未过期的验证码
        EmailCode emailCode = emailCodeMapper.selectValidCode(email, 1,LocalDateTime.now());
        if (emailCode == null) {
            return false; // 验证码不存在/已过期/已使用
        }
        // 2. 校验验证码
        boolean valid = emailCode.getCode().equals(code);
        if (valid) {
            // 3. 标记验证码为已使用
            emailCodeMapper.markUsed(emailCode.getId());
        }
        return valid;
    }
}