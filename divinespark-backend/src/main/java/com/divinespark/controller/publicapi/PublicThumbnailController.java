package com.divinespark.controller.publicapi;

import com.divinespark.service.ThumbnailService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/thumbnail")
public class PublicThumbnailController {

    private final ThumbnailService thumbnailService;

    public PublicThumbnailController(ThumbnailService thumbnailService) {
        this.thumbnailService = thumbnailService;
    }

    @GetMapping(
            value = "/{sessionId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<?> getThumbnail(@PathVariable String sessionId) {
        return thumbnailService.getThumbnail(sessionId);
    }
}
