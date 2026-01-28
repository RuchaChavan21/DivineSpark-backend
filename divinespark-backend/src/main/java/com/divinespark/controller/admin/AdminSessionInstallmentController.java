package com.divinespark.controller.admin;

import com.divinespark.dto.AdminSessionUserInstallmentResponse;
import com.divinespark.service.AdminInstallmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/sessions")
public class AdminSessionInstallmentController {

    private final AdminInstallmentService adminInstallmentService;

    public AdminSessionInstallmentController(AdminInstallmentService adminInstallmentService) {
        this.adminInstallmentService = adminInstallmentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{sessionId}/installments")
    public ResponseEntity<List<AdminSessionUserInstallmentResponse>> getSessionInstallments(
            @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(
                adminInstallmentService.getSessionInstallments(sessionId)
        );
    }
}
