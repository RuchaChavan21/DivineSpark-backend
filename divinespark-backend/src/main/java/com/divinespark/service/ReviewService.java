package com.divinespark.service;

import com.divinespark.dto.ReviewCreateRequest;
import com.divinespark.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {

    void submitReview(Long userId, ReviewCreateRequest request);

    ReviewResponse getUserReview(Long userId);

    List<ReviewResponse> getAllReviews();
}
