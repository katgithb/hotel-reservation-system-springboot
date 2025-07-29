package com.zenhotel.hrs_api.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zenhotel.hrs_api.enums.BookingStatus;
import com.zenhotel.hrs_api.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingSummaryDTO {

    private Long id;
    private Long userId;
    private Long roomId;
    private PaymentStatus paymentStatus;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BigDecimal totalPrice;
    private String bookingReference;
    private BookingStatus bookingStatus;
    private OffsetDateTime createdAt;

}
