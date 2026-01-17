package com.divinespark.service.impl;

import com.divinespark.entity.Session;
import com.divinespark.repository.SessionRepository;
import com.divinespark.service.ThumbnailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ThumbnailServiceImpl implements ThumbnailService {

    private final SessionRepository repo;

    public ThumbnailServiceImpl(SessionRepository repo) {
        this.repo = repo;
    }

    @Override
    public ResponseEntity<?> saveThumbnail(String id, MultipartFile file) {
        try {
            Session session = repo.findById(Long.valueOf(id))
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            // Set thumbnail data
            session.setThumbnailData(file.getBytes());
            session.setHasThumbnail(true);

            repo.save(session);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Thumbnail saved successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> updateThumbnail(String id, MultipartFile file) {
        try {
            Session session = repo.findById(Long.valueOf(id))
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            session.setThumbnailData(file.getBytes());
            session.setHasThumbnail(true);

            repo.save(session);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Thumbnail updated successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }


    @Override
    public ResponseEntity<?> deleteThumbnail(String id) {
        try {
            Session session = repo.findById(Long.valueOf(id))
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            session.setThumbnailData(null);
            session.setHasThumbnail(false);

            repo.save(session);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Thumbnail deleted successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getThumbnail(String id) {
        try {
            Session session = repo.findById(Long.valueOf(id))
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            if (!session.isHasThumbnail() || session.getThumbnailData() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Thumbnail not found");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // or IMAGE_PNG depending on your input
                    .body(session.getThumbnailData());

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }
}
