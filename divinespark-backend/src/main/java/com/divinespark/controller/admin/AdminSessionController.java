package com.divinespark.controller.admin;

import com.divinespark.dto.SessionCreateRequest;
import com.divinespark.dto.SessionListResponse;
import com.divinespark.dto.SessionUpdateRequest;

import com.divinespark.entity.Session;
import com.divinespark.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/sessions")
public class AdminSessionController {

    private final SessionService sessionService;

    public AdminSessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Session> create(@RequestBody SessionCreateRequest req) {
        return ResponseEntity.ok(sessionService.create(req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Session> update(
            @PathVariable Long id,
            @RequestBody SessionUpdateRequest req) {

        return ResponseEntity.ok(sessionService.update(id, req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sessionService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<SessionListResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Session> result = sessionService.getAll(page, size);

        SessionListResponse response = new SessionListResponse();
        response.setSessions(result.getContent());
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());

        return ResponseEntity.ok(response);
    }



}
