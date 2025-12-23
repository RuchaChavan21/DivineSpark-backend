package com.divinespark.repository;

import com.divinespark.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByUserIdAndSessionId(Long userId, Long sessionId);

    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.session
        WHERE b.user.id = :userId
        ORDER BY b.createdAt DESC
    """)
    List<Booking> findUserBookingsWithSession(Long userId);

    Optional<Booking> findById(Long id);

}
