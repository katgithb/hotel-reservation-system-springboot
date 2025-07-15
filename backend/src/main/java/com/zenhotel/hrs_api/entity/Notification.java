package com.zenhotel.hrs_api.entity;


import com.zenhotel.hrs_api.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "notifications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Recipient email is required")
    private String recipientEmail;

    private String recipientName;

    @NotBlank(message = "Body is required")
    private String body;

    @NotBlank(message = "Booking Reference is required")
    private String bookingReference;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @NotNull
    @PastOrPresent
    private final OffsetDateTime createdAt = OffsetDateTime.now();

}
