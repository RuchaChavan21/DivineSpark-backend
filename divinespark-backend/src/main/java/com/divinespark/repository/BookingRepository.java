package com.divinespark.repository;

import com.divinespark.dto.AdminSessionBookingResponse;
import com.divinespark.dto.AdminSessionUserResponse;
import com.divinespark.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    @Query("""
    SELECT new com.divinespark.dto.AdminSessionUserResponse(
        u.id,
        u.email,
        b.status
    )
    FROM Booking b
    JOIN b.user u
    WHERE b.session.id = :sessionId
""")
    List<AdminSessionUserResponse> findUsersBySessionId(
            @Param("sessionId") Long sessionId
    );

    @Query("""
    SELECT new com.divinespark.dto.AdminSessionBookingResponse(
        b.id,
        u.id,
        u.email,
        b.status,
        b.createdAt
    )
    FROM Booking b
    JOIN b.user u
    WHERE b.session.id = :sessionId
""")
    List<AdminSessionBookingResponse> findBookingsBySessionId(
            @Param("sessionId") Long sessionId
    );

}
