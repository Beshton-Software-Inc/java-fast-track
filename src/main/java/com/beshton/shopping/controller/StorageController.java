package com.beshton.shopping.controller;

import com.beshton.shopping.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("bucketName") String bucketName,
                             @RequestParam("key") String key,
                             @RequestParam("filePath") String filePath) {
        File file = new File(filePath);
        s3Service.uploadFile(bucketName, key, file);
        return "文件上传成功。";
    }

    @DeleteMapping("/delete")
    public String deleteFile(@RequestParam("bucketName") String bucketName,
                             @RequestParam("key") String key) {
        s3Service.deleteFile(bucketName, key);
        return "文件删除成功。";
    }
}
