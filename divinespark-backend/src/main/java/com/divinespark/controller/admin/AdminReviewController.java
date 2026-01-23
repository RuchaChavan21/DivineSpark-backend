package com.divinespark.controller.admin;

import com.divinespark.dto.ReviewCreateRequest;
import com.divinespark.dto.ReviewResponse;
import com.divinespark.entity.enums.ReviewStatus;
import com.divinespark.service.ReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/pending")
    public List<ReviewResponse> getPendingReviews() {
        return reviewService.getReviewsByStatus(ReviewStatus.PENDING);
    }

    @PutMapping("/{reviewId}")
    public void editReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewCreateRequest request) {

        reviewService.editReview(reviewId, request);
    }

    @PatchMapping("/{reviewId}/approve")
    public void approveReview(@PathVariable Long reviewId) {
        reviewService.approveReview(reviewId);
    }

    @PatchMapping("/{reviewId}/reject")
    public void rejectReview(@PathVariable Long reviewId) {
        reviewService.rejectReview(reviewId);
    }
}
