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

    // ---------- CREATE ----------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Session> create(
            @RequestBody SessionCreateRequest req) {

        return ResponseEntity.ok(sessionService.create(req));
    }

    // ---------- UPDATE ----------
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Session> update(
            @PathVariable(name = "id") Long id,
            @RequestBody SessionUpdateRequest req) {

        return ResponseEntity.ok(sessionService.update(id, req));
    }

    // ---------- DELETE ----------
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "id") Long id) {

        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- GET ALL ----------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<SessionListResponse> getAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<Session> result = sessionService.getAll(page, size);

        SessionListResponse response = new SessionListResponse();
        response.setSessions(result.getContent());
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // ---------- USERS BY SESSION ----------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/users")
    public ResponseEntity<List<AdminSessionUserResponse>> getUsersBySession(
            @PathVariable(name = "id") Long id) {

        return ResponseEntity.ok(sessionService.getUsersBySession(id));
    }

    // ---------- BOOKINGS BY SESSION ----------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<AdminSessionBookingResponse>> getBookingsBySession(
            @PathVariable(name = "id") Long id) {

        return ResponseEntity.ok(sessionService.getBookingsBySession(id));
    }

    // ---------- UPDATE STATUS ----------
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateSessionStatus(
            @PathVariable(name = "id") Long id,
            @RequestBody SessionStatusUpdateRequest request) {

        sessionService.updateStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    // ---------- UPLOAD RESOURCE ----------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            value = "/{id}/resources",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> uploadResource(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "fileType") String fileType,
            @RequestParam(name = "file") MultipartFile file) {

        sessionService.uploadResource(id, fileType, file);
        return ResponseEntity.noContent().build();
    }
}
