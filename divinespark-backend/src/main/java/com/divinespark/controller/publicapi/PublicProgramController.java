package com.divinespark.controller.publicapi;

import com.divinespark.service.ProgramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/programs")
public class PublicProgramController {

    private final ProgramService service;

    public PublicProgramController(ProgramService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getByCategory(
            @RequestParam String category) {
        return ResponseEntity.ok(service.getByCategory(category));
    }
}
