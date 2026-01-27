package com.divinespark.controller.user;

import com.divinespark.dto.*;
import com.divinespark.security.CustomUserDetails;
import com.divinespark.service.InstallementService;
import com.divinespark.service.SessionService;
import com.divinespark.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final SessionService sessionService;
    private final UserService userService;
    private final InstallementService installementService;

    public UserController(SessionService sessionService, UserService userService, InstallementService installementService) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.installementService = installementService;
    }

    @GetMapping("/sessions")
    public ResponseEntity<SessionUserListResponse> getSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {

        return ResponseEntity.ok(
                sessionService.getUpcomingSessions(page, size, type)
        );
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionDetailResponse> getSessionDetails(
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(
                sessionService.getSessionDetails(sessionId)
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/sessions/{sessionId}/join")
    public ResponseEntity<?> joinFreeSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        sessionService.joinFreeSession(sessionId, userDetails.getId());
        return ResponseEntity.ok(
                Map.of("message", "Joined successfully")
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/sessions/{sessionId}/pay")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(
                sessionService.initiatePaidSession(
                        sessionId, userDetails.getId())
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/sessions/{sessionId}/pay/installments")
    public ResponseEntity<InstallmentPaymentInitiateResponse> payInInstallments(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        InstallmentPaymentInitiateResponse response =
                sessionService.initiateInstallmentPayment(
                        sessionId,
                        userDetails.getId()
                );

        return ResponseEntity.ok(response);
    }



    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(
                userService.getUserProfile(userDetails.getId())
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateProfileRequest request) {

        userService.updateProfile(userDetails.getId(), request);
        return ResponseEntity.ok(
                Map.of("message", "Profile updated successfully")
        );
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/sessions/{sessionId}/whatsapp-link")
    public ResponseEntity<?> getWhatsappLink(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String link = sessionService.getWhatsappLinkIfConfirmed(
                sessionId, userDetails.getId()
        );

        return ResponseEntity.ok(
                Map.of("whatsappLink", link)
        );
    }


}
