package com.zenhotel.hrs_api.controller;

import com.zenhotel.hrs_api.payload.ApiResponse;
import com.zenhotel.hrs_api.payment.stripe.PaymentService;
import com.zenhotel.hrs_api.payment.stripe.payload.PaymentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse> createBookingPaymentIntent(
            @Valid @RequestBody PaymentRequest paymentRequest) {

        return ResponseEntity.ok(paymentService.createBookingPaymentIntent(paymentRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateBookingPayment(
            @Valid @RequestBody PaymentRequest paymentRequest) {
        
        return ResponseEntity.ok(paymentService.updateBookingPayment(paymentRequest));
    }

}
