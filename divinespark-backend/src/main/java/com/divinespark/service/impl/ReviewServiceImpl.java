package com.divinespark.service.impl;

import com.divinespark.dto.ReviewCreateRequest;
import com.divinespark.entity.Booking;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.ReviewRepository;
import com.divinespark.service.ReviewService;
import com.divinespark.entity.Review;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(
            BookingRepository bookingRepository,
            ReviewRepository reviewRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void submitReview(Long bookingId, Long userId, ReviewCreateRequest request) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Ownership check
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized review");
        }

        // Session must be COMPLETED
        if (!booking.getSession().getStatus().name().equals("COMPLETED")) {
            throw new RuntimeException("Session not completed yet");
        }

        // Prevent duplicate reviews
        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new RuntimeException("Review already submitted");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setUser(booking.getUser());
        review.setSession(booking.getSession());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        reviewRepository.save(review);
    }
}
