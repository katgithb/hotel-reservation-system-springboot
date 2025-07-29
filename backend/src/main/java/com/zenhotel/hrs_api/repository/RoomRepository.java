package com.zenhotel.hrs_api.repository;

import com.zenhotel.hrs_api.entity.Room;
import com.zenhotel.hrs_api.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumber(Integer roomNumber);

    @Query("""
            SELECT r FROM Room r
            WHERE r.id NOT IN (
            SELECT b.room.id
            FROM Booking b
            WHERE :checkInDate <= b.checkOutDate
            AND :checkOutDate >= b.checkInDate
            AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
            )
            AND (:roomType IS NULL OR r.type = :roomType)
            """)
    Page<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomType") RoomType roomType,
            Pageable pageable
    );

    @Query("""
                SELECT r FROM Room r
                WHERE CAST(r.roomNumber AS string) LIKE %:searchParam%
                OR LOWER(r.type) LIKE LOWER(:searchParam)
                OR CAST(r.pricePerNight AS string) LIKE %:searchParam%
                OR CAST(r.capacity AS string) LIKE %:searchParam%
                OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchParam, '%'))
            """)
    Page<Room> searchRooms(@Param("searchParam") String searchParam, Pageable pageable);
}
