package com.zenhotel.hrs_api.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zenhotel.hrs_api.enums.PaymentGateway;
import com.zenhotel.hrs_api.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {

    private Long id;
    private BookingDTO booking;
    private String transactionId;
    private BigDecimal amount;

    private PaymentGateway paymentGateway; //e,g Paypal. Stripe, Flutterwave
    private OffsetDateTime paymentDate;
    private PaymentStatus status; //pending, completed, failed, e.t.c

    private String bookingReference;
    private String failureReason;
    private String approvalLink; //paypal payment approval URL

}
