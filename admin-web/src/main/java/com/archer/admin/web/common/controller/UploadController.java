package com.archer.admin.web.common.controller;

import com.archer.admin.web.component.Result;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/common")
public class UploadController {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${file.upload.path:public}")
    private String uploadPath;

    @PostMapping(path = "/upload")
    public Result upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.fileError();
        }

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成新的文件名
            String newFilename = UUID.randomUUID().toString() + extension;

            // 创建上传目录
            String projectRoot = System.getProperty("user.dir");
            Path uploadDir = Paths.get(projectRoot, uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 构建返回URL
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String baseUrl = scheme + "://" + serverName;
            if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
                baseUrl += ":" + serverPort;
            }
            String fileUrl = baseUrl + contextPath + "/" + uploadPath + "/" + newFilename;

            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filename", newFilename);

            return Result.success(data);

        } catch (IOException e) {
            log.error("Upload error ", e);
        }
        return Result.error();
    }
}
