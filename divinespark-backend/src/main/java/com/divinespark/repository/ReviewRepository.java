package com.divinespark.repository;

import com.divinespark.entity.Review;
import com.divinespark.entity.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserId(Long userId);

    Optional<Review> findByUserId(Long userId);

    List<Review> findAllByOrderByCreatedAtDesc();

    List<Review> findByStatusOrderByCreatedAtDesc(ReviewStatus status);

    Optional<Review> findById(Long id);

}
