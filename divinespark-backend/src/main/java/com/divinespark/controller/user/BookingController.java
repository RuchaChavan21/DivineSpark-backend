package com.divinespark.controller.user;

import com.divinespark.dto.UserBookingResponse;
import com.divinespark.entity.User;
import com.divinespark.security.CustomUserDetails;
import com.divinespark.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<UserBookingResponse>> getUserBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                bookingService.getUserBookings(userDetails.getId())
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable(value = "id", required = true) Long bookingId,
            Authentication authentication) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        bookingService.cancelBooking(bookingId, userDetails.getId());

        return ResponseEntity.noContent().build();
    }




}
