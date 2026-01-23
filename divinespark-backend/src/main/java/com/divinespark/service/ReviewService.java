package com.divinespark.service;

import com.divinespark.dto.ReviewCreateRequest;
import com.divinespark.dto.ReviewResponse;
import com.divinespark.entity.enums.ReviewStatus;

import java.util.List;

public interface ReviewService {

    void submitReview(Long userId, ReviewCreateRequest request);

    ReviewResponse getUserReview(Long userId);

    List<ReviewResponse> getAllReviews();

    List<ReviewResponse> getReviewsByStatus(ReviewStatus status);

    void approveReview(Long reviewId);

    void rejectReview(Long reviewId);

    void editReview(Long reviewId, ReviewCreateRequest request);

}
