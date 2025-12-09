package com.underwater.mapper;

import com.underwater.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 用户Mapper（适配sys_user表，注解式SQL）
 */
@Repository
public interface UserMapper {
    /**
     * 插入用户（仅插入非自动生成的字段）
     */
    @Insert("INSERT INTO sys_user (username, password, email, email_verified, avatar, status) " +
            "VALUES (#{username}, #{password}, #{email}, #{emailVerified}, #{avatar}, #{status})")
    int insert(User user);

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User selectById(Long id);

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User selectByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    User selectByEmail(String email);

    /**
     * 根据邮箱+状态查询用户
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND status = #{status}")
    User selectByEmailAndStatus(String email, Integer status);
}