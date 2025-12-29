package com.divinespark.controller.admin;

import com.divinespark.dto.*;

import com.divinespark.entity.Session;
import com.divinespark.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            @PathVariable("id") Long id,
            @RequestBody SessionUpdateRequest req) {

        return ResponseEntity.ok(sessionService.update(id, req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id) {

        sessionService.delete(id);
        return ResponseEntity.noContent().build();
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


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/users")
    public ResponseEntity<List<AdminSessionUserResponse>> getUsersBySession(
            @PathVariable(value = "id", required = true) Long id) {

        return ResponseEntity.ok(sessionService.getUsersBySession(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<AdminSessionBookingResponse>> getBookingsBySession(
            @PathVariable(value = "id", required = true) Long id) {

        return ResponseEntity.ok(sessionService.getBookingsBySession(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateSessionStatus(
            @PathVariable(value = "id", required = true) Long id,
            @RequestBody SessionStatusUpdateRequest request) {

        sessionService.updateStatus(id, request.getStatus());
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping(
            value = "/{id}/resources",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> uploadResource(
            @PathVariable(value = "id", required = true) Long id,
            @RequestParam("fileType") String fileType,
            @RequestParam("file") MultipartFile file) {

        sessionService.uploadResource(id, fileType, file);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/past")
    public ResponseEntity<SessionListResponse> getPastSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Session> result = sessionService.getPastSessions(page, size);

        SessionListResponse response = new SessionListResponse();
        response.setSessions(result.getContent());
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());

        return ResponseEntity.ok(response);
    }

}
