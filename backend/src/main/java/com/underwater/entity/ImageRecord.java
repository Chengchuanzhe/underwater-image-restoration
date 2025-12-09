package com.underwater.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImageRecord {
    private Long id;             // 记录ID
    private Long userId;         // 用户ID
    private String originalFilename; // 原图文件名
    private String originalFilePath; // 原图存储路径
    private String originalFileUrl;  // 原图访问URL
    private String restoredFilePath; // 修复图存储路径
    private String restoredFileUrl;  // 修复图访问URL
    private Long fileSize;       // 文件大小（字节）
    private LocalDateTime restorationTime; // 修复时间
}