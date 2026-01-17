package com.divinespark.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ThumbnailService {

    ResponseEntity<?> saveThumbnail(String id, MultipartFile file);

    ResponseEntity<?> updateThumbnail(String id, MultipartFile file);

    ResponseEntity<?> deleteThumbnail(String id);

    ResponseEntity<?> getThumbnail(String id);
}
