package com.divinespark.controller.user;

import com.divinespark.dto.PaymentInitiateResponse;
import com.divinespark.dto.SessionDetailResponse;
import com.divinespark.dto.SessionUserListResponse;
import com.divinespark.entity.User;
import com.divinespark.repository.UserRepository;
import com.divinespark.security.CustomUserDetails;
import com.divinespark.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sessions")
public class UserController {

    private final SessionService sessionService;

    public UserController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<SessionUserListResponse> getSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {

        return ResponseEntity.ok(
                sessionService.getUpcomingSessions(page, size, type)
        );
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDetailResponse> getSessionDetails(
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(
                sessionService.getSessionDetails(sessionId)
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{sessionId}/join")
    public ResponseEntity<?> joinFreeSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        sessionService.joinFreeSession(sessionId, userDetails.getId());
        return ResponseEntity.ok(
                Map.of("message", "Joined successfully")
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{sessionId}/pay")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(
                sessionService.initiatePaidSession(
                        sessionId, userDetails.getId())
        );
    }
}
