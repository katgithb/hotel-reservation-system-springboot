package com.zenhotel.hrs_api.entity;


import com.zenhotel.hrs_api.enums.BookingStatus;
import com.zenhotel.hrs_api.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "bookings")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(cascade = CascadeType.REMOVE)
    // meaning when a user is deleted, all associated bookings of the user will be deleted
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    private LocalDate checkOutDate;

    @NotNull
    private BigDecimal totalPrice;

    @NotBlank(message = "Booking Reference is required")
    @Column(unique = true, nullable = false)
    private String bookingReference;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @NotNull
    @PastOrPresent
    private final OffsetDateTime createdAt = OffsetDateTime.now();

}
