package com.underwater;

import org.mybatis.spring.annotation.MapperScan;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.underwater.mapper")
public class UnderwaterApplication {
    static {
        // 加载OpenCV本地库（已放System32，无需改路径）
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        SpringApplication.run(UnderwaterApplication.class, args);
    }
}