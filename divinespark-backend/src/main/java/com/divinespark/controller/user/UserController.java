package com.divinespark.controller.user;

import com.divinespark.dto.PaymentInitiateResponse;
import com.divinespark.dto.SessionDetailResponse;
import com.divinespark.dto.SessionUserListResponse;
import com.divinespark.entity.User;
import com.divinespark.repository.UserRepository;
import com.divinespark.security.CustomUserDetails;
import com.divinespark.service.SessionService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<SessionUserListResponse> getSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type
    ) {
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

    @PostMapping("/{sessionId}/join")
    public ResponseEntity<?> joinFreeSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        sessionService.joinFreeSession(sessionId, userDetails.getId());
        return ResponseEntity.ok("Joined successfully");
    }

    @PostMapping("/{sessionId}/pay")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails ) {

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                sessionService.initiatePaidSession(sessionId, user.getId())
        );
    }

}
