package com.underwater.config;

import com.underwater.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // 导入 Value
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    // 注入配置文件中的图片上传路径
    @Value("${custom.image.upload-path}")
    private String uploadPath;

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register", "/email/code")
                .excludePathPatterns("/uploads/**")
                .excludePathPatterns("/error");
    }

    // 静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以 / 结尾
        String path = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";

        // 将 /uploads/** 的请求映射到配置的本地绝对路径
        // 注意：addResourceLocations 需要 "file:" 前缀来指定这是一个文件系统路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + path);
    }
}