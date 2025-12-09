package com.underwater.mapper;

import com.underwater.entity.EmailCode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EmailCodeMapper {
    // 插入验证码
    @Insert("INSERT INTO sys_email_code (email, code, type, expire_time, used) " +
            "VALUES (#{email}, #{code}, #{type}, #{expireTime}, #{used})")
    int insert(EmailCode emailCode);

    // 删除邮箱未使用的验证码
    @Delete("DELETE FROM sys_email_code WHERE email = #{email} AND used = 0 AND type = 1")
    int deleteUnusedByEmail(String email);

    // 查询有效验证码（未使用、未过期、类型为注册）
    @Select("SELECT * FROM sys_email_code WHERE email = #{email} AND type = #{type} " +
            "AND used = 0 AND expire_time > #{now} LIMIT 1")
    EmailCode selectValidCode(String email, Integer type, LocalDateTime now);

    // 标记验证码为已使用
    @Update("UPDATE sys_email_code SET used = 1 WHERE id = #{id}")
    int markUsed(Long id);
}