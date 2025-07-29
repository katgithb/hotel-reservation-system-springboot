package com.zenhotel.hrs_api.payment.stripe.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {

    @NotBlank(message = "Booking Reference is required")
    private String bookingReference;

    @NotNull(message = "Payment Amount is required")
    private BigDecimal amount;

    private String transactionId;
    private boolean success;
    private String failureReason;

}
