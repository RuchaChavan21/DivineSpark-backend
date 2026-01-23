package com.divinespark.service.impl;

import com.divinespark.dto.ReviewCreateRequest;
import com.divinespark.dto.ReviewResponse;
import com.divinespark.entity.Review;
import com.divinespark.entity.User;
import com.divinespark.entity.enums.ReviewStatus;
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setUserId(userId);
        review.setUserName(user.getUsername());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setStatus(ReviewStatus.PENDING);

        reviewRepository.save(review);
    }



    @Override
    public ReviewResponse getUserReview(Long userId) {

        Review review = reviewRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        ReviewResponse response = new ReviewResponse();
        response.setRating(review.getRating());
        response.setUserName(review.getUserName());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }

    @Override
    public List<ReviewResponse> getAllReviews() {

        List<Review> reviews =
                reviewRepository.findByStatusOrderByCreatedAtDesc(ReviewStatus.APPROVED);

        List<ReviewResponse> response = new ArrayList<>();

        for (Review r : reviews) {
            ReviewResponse dto = new ReviewResponse();
            dto.setRating(r.getRating());
            dto.setComment(r.getComment());
            dto.setUserName(r.getUserName());
            dto.setCreatedAt(r.getCreatedAt());

            User user = userRepository.findById(r.getUserId()).orElse(null);
            dto.setUserName(user != null ? user.getUsername() : "Anonymous");

            response.add(dto);
        }

        return response;
    }

    @Override
    public List<ReviewResponse> getReviewsByStatus(ReviewStatus status) {
        List<Review> reviews = reviewRepository.findByStatusOrderByCreatedAtDesc(status);
        List<ReviewResponse> list = new ArrayList<>();

        for (Review r : reviews) {
            ReviewResponse dto = new ReviewResponse();
            dto.setRating(r.getRating());
            dto.setComment(r.getComment());
            dto.setUserName(r.getUserName());
            dto.setCreatedAt(r.getCreatedAt());

            User user = userRepository.findById(r.getUserId()).orElse(null);
            dto.setUserName(user != null ? user.getUsername() : "Anonymous");

            list.add(dto);
        }
        return list;
    }

    @Override
    @Transactional
    public void approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setStatus(ReviewStatus.APPROVED);
    }

    @Override
    @Transactional
    public void rejectReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setStatus(ReviewStatus.REJECTED);
    }

    @Override
    @Transactional
    public void editReview(Long reviewId, ReviewCreateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
    }


}

