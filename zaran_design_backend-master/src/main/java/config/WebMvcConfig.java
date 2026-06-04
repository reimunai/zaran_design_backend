package com.example.zaran_design_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录的绝对路径
        String projectPath = System.getProperty("user.dir");
        // 拼接出 uploads 文件夹的完整路径
        String uploadPath = "file:" + projectPath + File.separator + "uploads" + File.separator;

        // 映射规则：凡是访问 /uploads/** 的请求，都去硬盘的 uploads 文件夹找
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}