package com.beshton.shopping.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class S3LoggingAspect {

    @Before("execution(* com.beshton.shopping.service.S3Service.uploadFile(..))")
    public void logBeforeUpload() {
        System.out.println("Files are being uploading to S3...");
    }

    @AfterReturning("execution(* com.beshton.shopping.service.S3Service.uploadFile(..))")
    public void logAfterUpload() {
        System.out.println("Successfully uploaded file to S3！");
    }

    @Before("execution(* com.beshton.shopping.service.S3Service.deleteFile(..))")
    public void logBeforeDelete() {
        System.out.println("Begin to delete file from S3...");
    }

    @AfterReturning("execution(* com.beshton.shopping.service.S3Service.deleteFile(..))")
    public void logAfterDelete() {
        System.out.println("Successfully deleted file from S3！");
    }
}
