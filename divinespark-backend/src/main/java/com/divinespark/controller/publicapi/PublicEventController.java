package com.divinespark.controller.publicapi;

import com.divinespark.dto.EventTickerResponse;
import com.divinespark.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/events")
public class PublicEventController {

    private final EventService service;

    public PublicEventController(EventService service) {
        this.service = service;
    }

    @GetMapping("/ticker")
    public ResponseEntity<List<EventTickerResponse>> getTicker() {
        return ResponseEntity.ok(service.getTickerEvents());
    }
}
