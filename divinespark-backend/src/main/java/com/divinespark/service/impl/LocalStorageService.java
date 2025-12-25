package com.divinespark.service.impl;

import com.divinespark.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalStorageService implements StorageService {

    private static final String BASE_PATH = "uploads/";

    @Override
    public String upload(MultipartFile file, String path) {
        try {
            Path dir = Paths.get(BASE_PATH + path);
            Files.createDirectories(dir);

            Path filePath = dir.resolve(file.getOriginalFilename());
            Files.write(filePath, file.getBytes());

            return "/uploads/" + path + "/" + file.getOriginalFilename();
        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }
    }
}
