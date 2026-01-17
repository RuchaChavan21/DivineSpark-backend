package com.divinespark.controller.admin;

import com.divinespark.service.impl.ThumbnailServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/thumbnail")
public class AdminThumbnailController {

    private final ThumbnailServiceImpl thumbnailService;

    public AdminThumbnailController(ThumbnailServiceImpl thumbnailService) {
        this.thumbnailService = thumbnailService;
    }

    @PostMapping(
            value = "/{sessionId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> saveThumbnail(
            @PathVariable String sessionId,
            @RequestPart("thumbnail") MultipartFile thumbnail) {

        return thumbnailService.saveThumbnail(sessionId, thumbnail);
    }

    @PutMapping(
            value = "/{sessionId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateThumbnail(
            @PathVariable String sessionId,
            @RequestPart("thumbnail") MultipartFile thumbnail) {

        return thumbnailService.updateThumbnail(sessionId, thumbnail);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getThumbnail(@PathVariable String sessionId) {
        return thumbnailService.getThumbnail(sessionId);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> deleteThumbnail(@PathVariable String sessionId) {
        return thumbnailService.deleteThumbnail(sessionId);
    }

}
