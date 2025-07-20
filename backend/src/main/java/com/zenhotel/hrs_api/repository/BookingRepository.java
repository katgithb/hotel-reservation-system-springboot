package com.zenhotel.hrs_api.repository;

import com.zenhotel.hrs_api.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Fetch all bookings for a specific user
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Optional<Booking> findByBookingReference(String bookingReference);

    @Query("""
               SELECT CASE WHEN COUNT(b) = 0 THEN true ELSE false END
               FROM Booking b
               WHERE b.room.id = :roomId
               AND :checkInDate <= b.checkOutDate
               AND :checkOutDate >= b.checkInDate
               AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
            """)
    boolean isRoomAvailable(@Param("roomId") Long roomId,
                            @Param("checkInDate") LocalDate checkInDate,
                            @Param("checkOutDate") LocalDate checkOutDate);
}
