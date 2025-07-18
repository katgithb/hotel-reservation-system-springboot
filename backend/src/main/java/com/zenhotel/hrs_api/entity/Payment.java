package com.zenhotel.hrs_api.entity;

import com.zenhotel.hrs_api.enums.PaymentGateway;
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
import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "payments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Transaction Id is required")
    private String transactionId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;

    @NotNull
    @PastOrPresent
    private OffsetDateTime paymentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @NotBlank(message = "Booking Reference is required")
    private String bookingReference;

    private String failureReason;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
