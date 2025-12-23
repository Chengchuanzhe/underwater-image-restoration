package com.underwater.service.impl;

import com.underwater.entity.ImageRecord;
import com.underwater.mapper.ImageRecordMapper;
import com.underwater.service.ImageService;
import org.opencv.core.Core;
import org.opencv.core.Scalar;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE; // 新增
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Service
public class ImageServiceImpl implements ImageService {
    @Value("${custom.image.upload-path}")
    private String uploadPath; // 图片上传本地路径

    @Value("${custom.image.base-url}")
    private String baseUrl;    // 图片访问基础URL

    @Resource
    private ImageRecordMapper recordMapper;

    @Override
    public ImageRecord upload(MultipartFile file, Long userId) throws Exception {
        // 1. 创建上传目录（不存在则创建）
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 生成唯一文件名（避免重复）
        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        String filePath = uploadPath + fileName;

        // 3. 保存文件到本地
        File dest = new File(filePath);
        file.transferTo(dest);

        // 4. 保存记录到数据库
        ImageRecord record = new ImageRecord();
        record.setUserId(userId);
        record.setOriginalFilename(originalName);
        record.setOriginalFilePath(filePath);
        record.setOriginalFileUrl(baseUrl + fileName);
        record.setFileSize(file.getSize());
        recordMapper.insert(record);

        return record;
    }

    @Override
    public ImageRecord restore(Long imageId, Long userId) {
        // 1. 查询图片记录（校验权限）
        ImageRecord record = recordMapper.selectById(imageId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在或无操作权限");
        }

        // 2. 读取原图
        Mat src = Imgcodecs.imread(record.getOriginalFilePath());
        if (src.empty()) {
            throw new RuntimeException("无法读取原始图片文件");
        }

        Mat dst = new Mat();

        // ================= 核心修复算法开始 =================

        // 步骤 2.1: 简易白平衡 (Gray World Algorithm) - 去除蓝绿色偏
        // 计算三个通道的平均值
        List<Mat> bgrChannels = new ArrayList<>();
        Core.split(src, bgrChannels);
        double bMean = Core.mean(bgrChannels.get(0)).val[0];
        double gMean = Core.mean(bgrChannels.get(1)).val[0];
        double rMean = Core.mean(bgrChannels.get(2)).val[0];

        // 计算全局平均灰度
        double k = (bMean + gMean + rMean) / 3.0;

        // 计算各通道增益系数
        double kb = k / bMean;
        double kg = k / gMean;
        double kr = k / rMean;

        // 应用增益（限制在0-255范围内）
        Core.multiply(bgrChannels.get(0), new Scalar(kb), bgrChannels.get(0));
        Core.multiply(bgrChannels.get(1), new Scalar(kg), bgrChannels.get(1));
        Core.multiply(bgrChannels.get(2), new Scalar(kr), bgrChannels.get(2));

        Mat balanced = new Mat();
        Core.merge(bgrChannels, balanced);

        // 步骤 2.2: 转换到 LAB 颜色空间应用 CLAHE - 增强对比度并去雾
        Mat lab = new Mat();
        Imgproc.cvtColor(balanced, lab, Imgproc.COLOR_BGR2Lab);

        List<Mat> labChannels = new ArrayList<>();
        Core.split(lab, labChannels);

        // 创建 CLAHE 对象 (ClipLimit: 限制对比度阈值, TileGridSize: 局部块大小)
        // 2.0 和 (8,8) 是常用经验值，如果觉得不够清晰可以调大 ClipLimit 到 3.0 或 4.0
        CLAHE clahe = Imgproc.createCLAHE(2.0, new Size(8, 8));

        // 只对 L 通道（亮度）进行均衡化，这样不会破坏颜色
        Mat dstL = new Mat();
        clahe.apply(labChannels.get(0), dstL);
        dstL.copyTo(labChannels.get(0));

        // 合并通道并转回 BGR
        Core.merge(labChannels, lab);
        Imgproc.cvtColor(lab, dst, Imgproc.COLOR_Lab2BGR);

        // 步骤 2.3: 轻微锐化 (可选，增强细节)
        Mat kernel = new Mat(3, 3, org.opencv.core.CvType.CV_32F);
        // 经典的锐化卷积核
        float[] kernelData = {
                0, -1, 0,
                -1, 5, -1,
                0, -1, 0
        };
        kernel.put(0, 0, kernelData);
        Imgproc.filter2D(dst, dst, -1, kernel);

        // 释放内存
        balanced.release();
        lab.release();
        dstL.release();
        kernel.release();
        for(Mat m : bgrChannels) m.release();
        for(Mat m : labChannels) m.release();

        // ================= 核心修复算法结束 =================

        // 3. 保存修复后图片 (使用之前的安全路径逻辑)
        String originalPath = record.getOriginalFilePath();
        int lastDotIndex = originalPath.lastIndexOf(".");
        String restoredPath = originalPath.substring(0, lastDotIndex) + "_restored" + originalPath.substring(lastDotIndex);

        Imgcodecs.imwrite(restoredPath, dst);

        // 4. 更新数据库记录
        String originalUrl = record.getOriginalFileUrl();
        int lastUrlDotIndex = originalUrl.lastIndexOf(".");
        String restoredUrl = originalUrl.substring(0, lastUrlDotIndex) + "_restored" + originalUrl.substring(lastUrlDotIndex);

        record.setRestoredFilePath(restoredPath);
        record.setRestoredFileUrl(restoredUrl);
        recordMapper.updateById(record);

        // 释放源图像
        src.release();
        dst.release();

        return record;
    }

    @Override
    public List<ImageRecord> getRecords(Long userId) {
        return recordMapper.selectByUserId(userId);
    }
}