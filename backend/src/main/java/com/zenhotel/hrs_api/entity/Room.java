package com.zenhotel.hrs_api.entity;

import com.zenhotel.hrs_api.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "rooms")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(value = 1, message = "Room Number must be at least 1")
    @Column(unique = true)
    private Integer roomNumber;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @NotNull
    @DecimalMin(value = "0.1", message = "Price per night is required")
    private BigDecimal pricePerNight;

    @NotNull
    @Min(value = 1, message = "capacity must be at least 1")
    private Integer capacity;

    private String description; //additional data for the room

    private String imageUploadId;

    private String imageUrl; //this will hold the room picture

    @NotNull
    @PastOrPresent
    private final OffsetDateTime createdAt = OffsetDateTime.now();
    
}
