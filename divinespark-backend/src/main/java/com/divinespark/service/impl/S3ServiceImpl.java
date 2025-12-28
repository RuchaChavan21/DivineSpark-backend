package com.divinespark.service.impl;

import com.divinespark.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Lazy
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String key = file.getOriginalFilename() + "_" + System.currentTimeMillis();

        PutObjectRequest putObjectRequest = PutObjectRequest
                                            .builder()
                                            .bucket(bucketName)
                                            .key(key)
                                            .contentType(file.getContentType())
                                            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return key;
    }
}
