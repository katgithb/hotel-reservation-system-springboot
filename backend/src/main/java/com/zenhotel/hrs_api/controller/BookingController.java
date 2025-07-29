package com.zenhotel.hrs_api.controller;

import com.zenhotel.hrs_api.payload.*;
import com.zenhotel.hrs_api.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Booking Management Endpoints")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER') ")
    public ResponseEntity<ApiResponse> createBooking(@RequestBody BookingDTO bookingDTO) {
        return new ResponseEntity<>(bookingService.createBooking(bookingDTO),
                HttpStatus.CREATED);
    }

    @GetMapping("/{referenceNo}")
    public ResponseEntity<ApiResponse> findBookingByReferenceNo(@PathVariable String referenceNo) {
        return ResponseEntity.ok(bookingService.findBookingByReferenceNo(referenceNo));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PagedResponse<BookingSummaryDTO>> getAllBookings(
            @ModelAttribute PageRequestDTO pageRequest) {

        return ResponseEntity.ok(bookingService.getAllBookings(pageRequest));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updateBooking(@RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.updateBooking(bookingDTO));
    }

}
