package com.zenhotel.hrs_api.payment.stripe;

import com.zenhotel.hrs_api.payload.ApiResponse;
import com.zenhotel.hrs_api.payment.stripe.payload.PaymentRequest;

public interface PaymentService {

    ApiResponse createBookingPaymentIntent(PaymentRequest paymentRequest);

    ApiResponse updateBookingPayment(PaymentRequest paymentRequest);
}
