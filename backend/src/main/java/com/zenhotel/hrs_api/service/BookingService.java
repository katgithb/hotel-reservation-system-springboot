package com.zenhotel.hrs_api.service;

import com.zenhotel.hrs_api.payload.*;

public interface BookingService {

    boolean existsBookingWithReferenceNo(String bookingReference);

    ApiResponse createBooking(BookingDTO bookingDTO);

    ApiResponse findBookingByReferenceNo(String bookingReference);

    PagedResponse<BookingSummaryDTO> getAllBookings(PageRequestDTO pageRequest);

    ApiResponse updateBooking(BookingDTO bookingDTO);
}
