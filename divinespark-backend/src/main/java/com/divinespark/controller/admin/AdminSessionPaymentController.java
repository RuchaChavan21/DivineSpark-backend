package com.divinespark.controller.admin;



import com.divinespark.dto.AdminSessionOverviewResponse;
import com.divinespark.service.AdminSessionPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/sessions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSessionPaymentController {

    private final AdminSessionPaymentService service;

    public AdminSessionPaymentController(
            AdminSessionPaymentService service
    ) {
        this.service = service;
    }

    @GetMapping("/{sessionId}/overview")
    public ResponseEntity<AdminSessionOverviewResponse> overview(
            @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(
                service.getSessionOverview(sessionId)
        );
    }
}
