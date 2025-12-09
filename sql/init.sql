
CREATE DATABASE IF NOT EXISTS underwater_db
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE underwater_db;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT COMMENT '用户主键ID' PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密存储）',
    email VARCHAR(100) NOT NULL COMMENT '用户邮箱（唯一）',
    email_verified TINYINT DEFAULT 0 COMMENT '邮箱验证状态：0-未验证 1-已验证',
    avatar VARCHAR(512) DEFAULT '' COMMENT '用户头像URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '账号状态：1-正常 0-禁用',
    -- 唯一索引（避免重复注册）
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 建邮箱验证码表（注册验证用）
CREATE TABLE IF NOT EXISTS sys_email_code (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY,
    email VARCHAR(100) NOT NULL COMMENT '接收验证码的邮箱',
    code VARCHAR(10) NOT NULL COMMENT '6位数字验证码',
    type TINYINT NOT NULL COMMENT '验证码类型：1-注册 2-找回密码',
    expire_time DATETIME NOT NULL COMMENT '验证码过期时间（默认15分钟）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    used TINYINT DEFAULT 0 COMMENT '是否使用：0-未使用 1-已使用',
    -- 索引（优化查询）
    INDEX idx_email_type (email, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱验证码表';

-- 创建图像修复记录表（关联用户）
CREATE TABLE IF NOT EXISTS image_restoration_record (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '关联用户ID（sys_user.id）',
    original_filename VARCHAR(255) NOT NULL COMMENT '原图文件名',
    original_file_path VARCHAR(512) NOT NULL COMMENT '原图本地存储路径',
    original_file_url VARCHAR(512) NOT NULL COMMENT '原图访问URL',
    restored_file_path VARCHAR(512) NOT NULL COMMENT '修复图本地存储路径',
    restored_file_url VARCHAR(512) NOT NULL COMMENT '修复图访问URL',
    file_size BIGINT COMMENT '文件大小（字节）',
    restoration_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '修复时间',
    status TINYINT DEFAULT 1 COMMENT '修复状态：1-成功 0-失败',
    remark VARCHAR(512) DEFAULT '' COMMENT '备注（失败原因等）',
    -- 索引（优化查询）
    INDEX idx_user_id (user_id),
    INDEX idx_restoration_time (restoration_time),
    -- 外键（删除用户时同步删除记录）
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='水下图像修复记录表';
