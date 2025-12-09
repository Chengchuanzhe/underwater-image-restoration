package com.underwater.controller;

import com.underwater.entity.ImageRecord;
import com.underwater.entity.Result;
import com.underwater.service.ImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Resource
    private ImageService imageService;

    // 上传图片
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam MultipartFile file, @RequestAttribute Long userId) {
        try {
            ImageRecord record = imageService.upload(file, userId);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }

    // 修复图片
    @PostMapping("/restore")
    public Result<?> restore(@RequestParam Long imageId, @RequestAttribute Long userId) {
        try {
            ImageRecord record = imageService.restore(imageId, userId);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error("图片修复失败：" + e.getMessage());
        }
    }

    // 查询用户的修复记录
    @GetMapping("/records")
    public Result<?> getRecords(@RequestAttribute Long userId) {
        List<ImageRecord> records = imageService.getRecords(userId);
        return Result.success(records);
    }
}