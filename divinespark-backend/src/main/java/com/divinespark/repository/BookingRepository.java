package com.divinespark.repository;

import com.divinespark.dto.AdminSessionBookingResponse;
import com.divinespark.dto.AdminSessionUserResponse;
import com.divinespark.entity.Booking;
import com.divinespark.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ---------- USER BOOKINGS ----------
    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.session
        WHERE b.user.id = :userId
        ORDER BY b.createdAt DESC
    """)
    List<Booking> findUserBookingsWithSession(Long userId);

    Optional<Booking> findByUser_IdAndSession_Id(
            Long userId,
            Long sessionId
    );

    boolean existsByUser_IdAndSession_Id(
            Long userId,
            Long sessionId
    );

    boolean existsByUser_IdAndSession_IdAndBookingStatus(
            Long userId,
            Long sessionId,
            BookingStatus bookingStatus
    );

    // ---------- ADMIN ----------
    @Query("""
        SELECT new com.divinespark.dto.AdminSessionUserResponse(
            u.id,
            u.email,
            b.bookingStatus,
            u.username,
            u.contactNumber
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
            u.username,
            u.contactNumber,
            u.email,
            b.bookingStatus,
            b.createdAt
        )
        FROM Booking b
        JOIN b.user u
        WHERE b.session.id = :sessionId
    """)
    List<AdminSessionBookingResponse> findBookingsBySessionId(
            @Param("sessionId") Long sessionId
    );

    List<Booking> findByBookingStatusAndCreatedAtBefore(
            BookingStatus status,
            OffsetDateTime cutoffTime
    );

    boolean existsByUserIdAndSessionId(Long userId, Long sessionId);


    @Query("""
SELECT b FROM Booking b
JOIN FETCH b.user
JOIN FETCH b.session
WHERE b.session.id = :sessionId
""")
    List<Booking> findDetailedBookingsBySessionId(
            @Param("sessionId") Long sessionId
    );
    @Query("""
    SELECT b FROM Booking b
    JOIN FETCH b.user
    WHERE b.session.id = :sessionId
""")
    List<Booking> findBookingEntitiesBySessionId(
            @Param("sessionId") Long sessionId
    );


}
