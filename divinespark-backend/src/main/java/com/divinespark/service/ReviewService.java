package com.divinespark.service;

import com.divinespark.dto.ReviewCreateRequest;

public interface ReviewService {
    void submitReview(Long bookingId, Long userId, ReviewCreateRequest request);
}
