package com.divinespark.controller.admin;

import com.divinespark.dto.*;
import com.divinespark.service.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/donations")
public class AdminDonationController {

    private final DonationService donationService;

    public AdminDonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AdminDonationResponse>> getAllDonations() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<DonationStatsResponse> getStats() {
        DonationStatsResponse res = new DonationStatsResponse();
        res.setTotalAmount(donationService.getTotalDonatedAmount());
        return ResponseEntity.ok(res);
    }
}
