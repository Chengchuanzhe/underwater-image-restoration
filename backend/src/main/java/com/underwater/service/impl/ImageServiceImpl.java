package com.underwater.service.impl;

import com.underwater.entity.ImageRecord;
import com.underwater.mapper.ImageRecordMapper;
import com.underwater.service.ImageService;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
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

        // 2. OpenCV图片修复（去模糊+增强+锐化）
        Mat src = Imgcodecs.imread(record.getOriginalFilePath());
        Mat dst = new Mat();

        // 2.1 双边滤波去模糊
        Imgproc.bilateralFilter(src, dst, 9, 75, 75);
        // 2.2 直方图均衡化增强对比度
        Mat ycrcb = new Mat();
        Imgproc.cvtColor(dst, ycrcb, Imgproc.COLOR_BGR2YCrCb); // 转YCrCb颜色空间
        List<Mat> channels = new ArrayList<>(); // 用List<Mat>替代Mat[]，匹配OpenCV API参数要求
        Core.split(ycrcb, channels); // 拆分通道到List

        Imgproc.equalizeHist(channels.get(0), channels.get(0)); // 对亮度通道（Y通道）做直方图均衡化
        Core.merge(channels, ycrcb); // 合并通道

        Imgproc.cvtColor(ycrcb, dst, Imgproc.COLOR_YCrCb2BGR); // 转回BGR颜色空间
        // 2.3 形态学操作锐化
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(dst, dst, kernel);
        Imgproc.dilate(dst, dst, kernel);

        // 3. 保存修复后图片
        String originalPath = record.getOriginalFilePath();
        String restoredPath = originalPath.replace(".", "_restored.");
        Imgcodecs.imwrite(restoredPath, dst);

        // 4. 更新数据库记录
        String restoredUrl = record.getOriginalFileUrl().replace(".", "_restored.");
        record.setRestoredFilePath(restoredPath);
        record.setRestoredFileUrl(restoredUrl);
        recordMapper.updateById(record);

        return record;
    }

    @Override
    public List<ImageRecord> getRecords(Long userId) {
        return recordMapper.selectByUserId(userId);
    }
}