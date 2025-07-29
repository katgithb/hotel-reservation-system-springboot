package com.zenhotel.hrs_api.service.impl;

import com.zenhotel.hrs_api.entity.Booking;
import com.zenhotel.hrs_api.entity.Room;
import com.zenhotel.hrs_api.entity.User;
import com.zenhotel.hrs_api.enums.BookingStatus;
import com.zenhotel.hrs_api.enums.PaymentStatus;
import com.zenhotel.hrs_api.exception.InvalidBookingException;
import com.zenhotel.hrs_api.exception.RequestValidationException;
import com.zenhotel.hrs_api.exception.ResourceNotFoundException;
import com.zenhotel.hrs_api.payload.*;
import com.zenhotel.hrs_api.repository.BookingRepository;
import com.zenhotel.hrs_api.repository.RoomRepository;
import com.zenhotel.hrs_api.service.BookingReferenceGenerator;
import com.zenhotel.hrs_api.service.BookingService;
import com.zenhotel.hrs_api.service.NotificationService;
import com.zenhotel.hrs_api.service.UserService;
import com.zenhotel.hrs_api.utils.UrlValidatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final BookingReferenceGenerator bookingReferenceGenerator;
    private final ModelMapper modelMapper;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Override
    public boolean existsBookingWithReferenceNo(String bookingReference) {
        return bookingRepository.existsByBookingReference(bookingReference);
    }

    @Override
    @Transactional
    public ApiResponse createBooking(BookingDTO bookingDTO) {
        User currentUser = userService.getCurrentLoggedInUser();
        Long roomId = bookingDTO.getRoomId();
        LocalDate checkInDate = bookingDTO.getCheckInDate();
        LocalDate checkOutDate = bookingDTO.getCheckOutDate();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id [%s] not found".formatted(roomId)));

        validateBookingDates(room.getId(), checkInDate, checkOutDate);

        // calculate the total price for the stay
        BigDecimal totalPrice = calculateTotalPriceForStay(room.getPricePerNight(),
                checkInDate, checkOutDate);
        String bookingReferenceCode = bookingReferenceGenerator.generateBookingReference();

        // create the booking
        Booking booking = Booking.builder()
                .user(currentUser)
                .room(room)
                .checkInDate(bookingDTO.getCheckInDate())
                .checkOutDate(bookingDTO.getCheckOutDate())
                .totalPrice(totalPrice)
                .bookingReference(bookingReferenceCode)
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        // save booking
        Booking savedBooking = bookingRepository.save(booking);

        BookingDTO savedBookingDTO = modelMapper.map(savedBooking, BookingDTO.class);

        // send booking confirmation email
        sendBookingConfirmationEmail(currentUser, bookingReferenceCode, totalPrice);

        return ApiResponse.builder()
                .status(201)
                .message("Booking created successfully")
                .payload(Map.of("booking", savedBookingDTO))
                .build();
    }

    @Override
    public ApiResponse findBookingByReferenceNo(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with reference no: %s not found".formatted(bookingReference)));

        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);

        return ApiResponse.builder()
                .status(200)
                .message("success")
                .payload(Map.of("booking", bookingDTO))
                .build();
    }

    @Override
    public PagedResponse<BookingSummaryDTO> getAllBookings(PageRequestDTO pageRequest) {
        Pageable pageable = pageRequest.toPageable();
        Page<Booking> bookingPage = bookingRepository.findAll(pageable);

        return PagedResponse.fromPage(
                200,
                "success",
                bookingPage,
                modelMapper,
                new TypeToken<List<BookingSummaryDTO>>() {
                });
    }

    @Override
    @Transactional
    public ApiResponse updateBooking(BookingDTO bookingDTO) {
        Long bookingId = Optional.ofNullable(bookingDTO.getId())
                .orElseThrow(() -> new RequestValidationException("Booking ID is required"));

        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id [%s] not found".formatted(bookingId)));

        Optional.ofNullable(bookingDTO.getBookingStatus())
                .ifPresent(existingBooking::setBookingStatus);

        Optional.ofNullable(bookingDTO.getPaymentStatus())
                .ifPresent(existingBooking::setPaymentStatus);

        bookingRepository.save(existingBooking);

        return ApiResponse.builder()
                .status(200)
                .message("Booking updated successfully")
                .build();
    }

    private void validateBookingDates(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        // validate check in and check out dates
        List<String> errors = Stream.of(
                        checkInDate.isBefore(LocalDate.now()) ? "Check in date cannot be in the past" : null,
                        checkOutDate.isBefore(checkInDate) ? "Check out date cannot be before check in date" : null,
                        checkInDate.isEqual(checkOutDate) ? "Check in and check out dates cannot be the same" : null
                ).filter(Objects::nonNull)
                .toList();

        if (!errors.isEmpty()) {
            throw new InvalidBookingException(errors.size() == 1
                    ? errors.getFirst() : errors.toString());
        }

        // validate room availability
        boolean isAvailable = bookingRepository.isRoomAvailable(roomId, checkInDate, checkOutDate);
        if (!isAvailable) {
            throw new InvalidBookingException("Room is not available for booking from [%s] to [%s]"
                    .formatted(checkInDate, checkOutDate));
        }
    }

    private void sendBookingConfirmationEmail(User currentUser, String bookingReferenceCode, BigDecimal totalPrice) {
        appBaseUrl = appBaseUrl != null ? appBaseUrl : "";

        // validate base url
        boolean isValidBaseUrl = UrlValidatorUtil.isValidHttpUrl(appBaseUrl);
        if (!isValidBaseUrl) {
            throw new RequestValidationException("Invalid base url: " + appBaseUrl + ". Please provide a valid base url.");
        }

        // construct the payment url with total price and reference code
        String paymentUrl = appBaseUrl + "/payment/" + bookingReferenceCode + "/" + totalPrice;
        log.info("BOOKING PAYMENT LINK: {}", paymentUrl);

        // construct the notification
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipientEmail(currentUser.getEmail())
                .recipientName(currentUser.getFirstName())
                .subject("Booking Confirmation")
                .body(String.format("Your booking has been created successfully with the reference code: %s. Please proceed with your payment using the payment link below. "
                        + "\n%s", bookingReferenceCode, paymentUrl))
                .bookingReference(bookingReferenceCode)
                .build();

        String bookingConfirmationTemplateId = "7152637";
        Properties params = new Properties();
        params.setProperty("name", currentUser.getFirstName());
        params.setProperty("booking_reference_code", " " + bookingReferenceCode);
        params.setProperty("booking_payment_link", paymentUrl);
        params.setProperty("templateId", bookingConfirmationTemplateId);

        // send notification via email
        notificationService.sendEmail(notificationDTO, params);
    }

    private BigDecimal calculateTotalPriceForStay(BigDecimal pricePerNight, LocalDate checkInDate, LocalDate checkOutDate) {
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }

}
