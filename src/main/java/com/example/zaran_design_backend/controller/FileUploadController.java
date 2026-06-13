package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 1. 检查文件是不是空的
        if (file.isEmpty()) {
            return Result.fail("上传失败，请选择要上传的图片！");
        }

        try {
            // 2. 获取你电脑上本项目的根目录，并指定 uploads 文件夹
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 如果 uploads 文件夹不存在，系统会自动帮你建一个
            }

            // 3. 获取图片原本的名字，提取后缀名（比如 .jpg, .png）
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 4. 用 UUID 给图片重新起个全球唯一的名字（防止不同用户上传同名图片发生覆盖）
            String newFilename = UUID.randomUUID().toString() + extension;

            // 5. 将前端传来的文件真正存入你的电脑硬盘
            File destFile = new File(uploadDir + newFilename);
            file.transferTo(destFile);

            // 6. 拼接出可以用浏览器访问这张图片的完整网络地址（默认端口 8080）
            String imageUrl = "http://localhost:8080/uploads/" + newFilename;

            // 7. 把这个网络地址发回给前端
            return Result.ok("图片上传成功！", imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("服务器异常，图片上传失败！");
        }
    }
}