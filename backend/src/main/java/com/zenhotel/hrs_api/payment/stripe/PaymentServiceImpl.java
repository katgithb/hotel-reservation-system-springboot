package com.zenhotel.hrs_api.payment.stripe;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.zenhotel.hrs_api.entity.Booking;
import com.zenhotel.hrs_api.entity.Payment;
import com.zenhotel.hrs_api.entity.User;
import com.zenhotel.hrs_api.enums.PaymentGateway;
import com.zenhotel.hrs_api.enums.PaymentStatus;
import com.zenhotel.hrs_api.exception.PaymentGatewayException;
import com.zenhotel.hrs_api.exception.RequestValidationException;
import com.zenhotel.hrs_api.exception.ResourceNotFoundException;
import com.zenhotel.hrs_api.payload.ApiResponse;
import com.zenhotel.hrs_api.payload.NotificationDTO;
import com.zenhotel.hrs_api.payment.stripe.payload.PaymentRequest;
import com.zenhotel.hrs_api.repository.BookingRepository;
import com.zenhotel.hrs_api.repository.PaymentRepository;
import com.zenhotel.hrs_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${stripe.api.secret-key}")
    private String stripeApiSecretKey;

    @Override
    public ApiResponse createBookingPaymentIntent(PaymentRequest paymentRequest) {
        log.info("Inside createBookingPaymentIntent()");
        Stripe.apiKey = stripeApiSecretKey;
        String bookingReference = paymentRequest.getBookingReference();
        BigDecimal amount = paymentRequest.getAmount();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with reference no: %s not found".formatted(bookingReference)));

        // check if amount from request matches the booking amount
        if (amount.compareTo(booking.getTotalPrice()) != 0) {
            throw new RequestValidationException("Payment amount does not match the booking amount");
        }

        // check if payment has already been made
        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new RequestValidationException("Payment for this booking has already been made");
        }

        try {
            // set the PaymentIntent params with the payment amount and currency
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue()) //amount in cents
                    .setCurrency("usd")
                    .putMetadata("bookingReference", bookingReference)
                    .build();

            // create the PaymentIntent
            PaymentIntent intent = PaymentIntent.create(params);
            String clientSecret = intent.getClientSecret();

            return ApiResponse.builder()
                    .status(200)
                    .message("success")
                    .payload(Map.of("client_secret", clientSecret))
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PaymentGatewayException("An unexpected error occurred while creating payment intent. Please try again.");
        }
    }

    @Override
    @Transactional
    public ApiResponse updateBookingPayment(PaymentRequest paymentRequest) {
        log.info("Inside updateBookingPayment()");
        String bookingReference = paymentRequest.getBookingReference();
        BigDecimal amount = paymentRequest.getAmount();
        PaymentStatus paymentStatus = paymentRequest.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with reference no: %s not found".formatted(bookingReference)));

        // check if amount from request matches the booking amount
        if (amount.compareTo(booking.getTotalPrice()) != 0) {
            throw new RequestValidationException("Payment amount does not match the booking amount");
        }

        // check if payment has already been made
        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new RequestValidationException("Payment for this booking has already been made");
        }

        Payment payment = Payment.builder()
                .paymentGateway(PaymentGateway.STRIPE)
                .amount(amount)
                .transactionId(paymentRequest.getTransactionId())
                .paymentStatus(paymentStatus)
                .paymentDate(OffsetDateTime.now())
                .bookingReference(bookingReference)
                .user(booking.getUser())
                .build();

        // set failure reason if payment is not successful
        if (!paymentRequest.isSuccess()) {
            payment.setFailureReason(paymentRequest.getFailureReason());
        }

        // save payment
        paymentRepository.save(payment);

        // update booking with updated payment status
        booking.setPaymentStatus(paymentStatus);
        bookingRepository.save(booking);

        log.info("About to send notification via email inside updateBookingPayment()");

        // send booking payment status email
        sendBookingPaymentStatusEmail(booking.getUser(), bookingReference,
                paymentRequest.isSuccess(), paymentRequest.getFailureReason());

        return ApiResponse.builder()
                .status(200)
                .message("Booking payment updated successfully")
                .build();
    }

    private void sendBookingPaymentStatusEmail(User user, String bookingReference,
                                               boolean paymentSuccess, String failureReason) {
        // construct email subject and body based on payment success
        final String subject = paymentSuccess
                ? "Booking Payment Successful" : "Booking Payment Failed";
        final String body = paymentSuccess
                ? String.format("Congratulation! Your payment for booking with reference: %s was successful.", bookingReference)
                : String.format("Sorry! Your payment for booking with reference: %s failed. Reason: %s", bookingReference, failureReason);

        // construct the notification
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipientEmail(user.getEmail())
                .recipientName(user.getFirstName())
                .subject(subject)
                .body(body)
                .bookingReference(bookingReference)
                .build();

        // send notification via email
        notificationService.sendEmail(notificationDTO);
    }

}
