package com.underwater.service;

import com.underwater.entity.ImageRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    // 上传图片
    ImageRecord upload(MultipartFile file, Long userId) throws Exception;

    // 修复图片
    ImageRecord restore(Long imageId, Long userId);

    // 查询用户的修复记录
    List<ImageRecord> getRecords(Long userId);
}