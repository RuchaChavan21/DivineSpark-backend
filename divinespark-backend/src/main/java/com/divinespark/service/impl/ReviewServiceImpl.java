package com.divinespark.service.impl;

import com.divinespark.dto.ReviewCreateRequest;
import com.divinespark.dto.ReviewResponse;
import com.divinespark.entity.Review;
import com.divinespark.entity.User;
import com.divinespark.repository.ReviewRepository;
import com.divinespark.repository.UserRepository;
import com.divinespark.service.ReviewService;
import com.divinespark.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void submitReview(Long userId, ReviewCreateRequest request) {

        if (reviewRepository.existsByUserId(userId)) {
            throw new IllegalStateException("User has already submitted a review");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        reviewRepository.save(review);
    }

    @Override
    public ReviewResponse getUserReview(Long userId) {

        Review review = reviewRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        ReviewResponse response = new ReviewResponse();
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }

    @Override
    public List<ReviewResponse> getAllReviews() {

        List<Review> reviews = reviewRepository.findAllByOrderByCreatedAtDesc();
        List<ReviewResponse> response = new ArrayList<>();

        for (Review r : reviews) {
            ReviewResponse dto = new ReviewResponse();
            dto.setRating(r.getRating());
            dto.setComment(r.getComment());
            dto.setCreatedAt(r.getCreatedAt());

            if (r.getUserId() != null) {
                User user = userRepository.findById(r.getUserId()).orElse(null);
                dto.setUserName(user != null ? user.getUsername() : "Anonymous");
            } else {
                dto.setUserName("Anonymous");
            }

            response.add(dto);
        }

        return response;
    }


}

