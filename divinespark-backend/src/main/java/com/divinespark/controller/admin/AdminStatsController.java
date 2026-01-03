package com.divinespark.controller.admin;

import com.divinespark.dto.AdminStatsResponse;
import com.divinespark.service.BookingService;
import com.divinespark.service.SessionService;
import com.divinespark.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminStatsController {

    private final UserService userService;
    private final BookingService bookingService;
    private final SessionService sessionService;

    public AdminStatsController(
            UserService userService,
            BookingService bookingService,
            SessionService sessionService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.sessionService = sessionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getAdminStats() {

        AdminStatsResponse response = new AdminStatsResponse();
        response.setTotalUsers(userService.getTotalUsers());
        response.setTotalBookings(bookingService.getTotalBookings());
        response.setUpcomingSessions(sessionService.getUpcomingSessionCount());
        response.setPastSessions(sessionService.getPastSessionCount());

        return ResponseEntity.ok(response);
    }
}
