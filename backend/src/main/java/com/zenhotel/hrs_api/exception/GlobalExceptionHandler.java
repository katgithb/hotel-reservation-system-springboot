package com.zenhotel.hrs_api.exception;

import com.zenhotel.hrs_api.payload.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException e,
                                                                          HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                "Invalid credentials. Please try again.",
                "INVALID_CREDENTIALS");

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException e,
                                                                               HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                "Access denied. Insufficient permissions to access this resource.",
                "ACCESS_DENIED");

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                                  HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        // Extract all validation error messages
        List<String> errorMessages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        // Combine error messages into a single message string
        String validationErrorMessage = errorMessages.toString();

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                validationErrorMessage,
                "INVALID_REQUEST");

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e,
                                                                            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                e.getMessage(),
                "RESOURCE_NOT_FOUND");

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleRequestValidationException(RequestValidationException e,
                                                                             HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                e.getMessage(),
                "INVALID_REQUEST");

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidBookingException(InvalidBookingException e,
                                                                          HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                e.getMessage(),
                "INVALID_REQUEST");

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResourceException(DuplicateResourceException e,
                                                                             HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                e.getMessage(),
                "DUPLICATE_RESOURCE");

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(CloudinaryResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleCloudinaryResourceException(CloudinaryResourceException e,
                                                                              HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                e.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailDeliveryException(EmailDeliveryException e,
                                                                         HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                e.getMessage(),
                "EMAIL_DELIVERY_ERROR");

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentGatewayException(PaymentGatewayException e,
                                                                          HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                e.getMessage(),
                "PAYMENT_GATEWAY_ERROR");

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception e,
                                                                   HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                e.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }

}
