package com.divinespark.controller.user;

import com.divinespark.dto.*;
import com.divinespark.security.CustomUserDetails;
import com.divinespark.service.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<RazorpayOrderResponse> createDonation(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody DonationCreateRequest request
    ) {
        return ResponseEntity.ok(
                donationService.createDonationOrder(user.getId(), request)
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/verify")
    public ResponseEntity<Void> verifyDonation(
            @RequestBody DonationVerifyRequest request
    ) {
        donationService.confirmDonationPayment(request);
        return ResponseEntity.ok().build();
    }
}



