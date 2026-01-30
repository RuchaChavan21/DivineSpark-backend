package com.divinespark.controller.user;

import com.divinespark.dto.InstallmentPayResponse;
import com.divinespark.dto.InstallmentResponse;
import com.divinespark.security.CustomUserDetails;

import com.divinespark.service.InstallementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/installments")
public class UserInstallmentController {

    private final InstallementService installmentService;

    public UserInstallmentController(InstallementService installmentService) {
        this.installmentService = installmentService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<InstallmentResponse>> getInstallmentsByBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        return ResponseEntity.ok(
                installmentService.getInstallmentsByBooking(
                        bookingId,
                        userDetails.getId()
                )
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{installmentId}/pay")
    public ResponseEntity<InstallmentPayResponse> payInstallment(
            @PathVariable Long installmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                installmentService.payInstallment(
                        installmentId,
                        userDetails.getId()
                )
        );
    }

}
