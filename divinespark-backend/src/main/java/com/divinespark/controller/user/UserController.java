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

@RestController
@RequestMapping("/api/v1/sessions")
public class UserController {

    private final SessionService sessionService;
    private final UserRepository userRepository;

    public UserController(SessionService sessionService, UserRepository userRepository) {
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    // ---------- LIST SESSIONS ----------
    @GetMapping
    public ResponseEntity<SessionUserListResponse> getSessions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "type", required = false) String type) {

        return ResponseEntity.ok(
                sessionService.getUpcomingSessions(page, size, type)
        );
    }

    // ---------- SESSION DETAILS ----------
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDetailResponse> getSessionDetails(
            @PathVariable(name = "sessionId") Long sessionId) {

        return ResponseEntity.ok(
                sessionService.getSessionDetails(sessionId)
        );
    }

    // ---------- JOIN FREE SESSION ----------
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{sessionId}/join")
    public ResponseEntity<?> joinFreeSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        sessionService.joinFreeSession(sessionId, userDetails.getId());
        return ResponseEntity.ok("Joined successfully");
    }


    // ---------- PAY FOR SESSION ----------
    @PostMapping("/{sessionId}/pay")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @PathVariable("sessionId") Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                sessionService.initiatePaidSession(sessionId, userDetails.getId())
        );
    }


}
