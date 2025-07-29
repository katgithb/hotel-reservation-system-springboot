package com.zenhotel.hrs_api.service;

import com.zenhotel.hrs_api.entity.BookingReference;
import com.zenhotel.hrs_api.repository.BookingReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BookingReferenceGenerator {

    private final BookingReferenceRepository bookingReferenceRepository;
    private static final String UPPER_ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    private static final int ALPHANUMERIC_CODE_LENGTH = 10;

    public String generateBookingReference() {
        String bookingReference;

        // keep generating until a unique booking reference code is found
        do {
            bookingReference = generateRandomAlphanumericCode(ALPHANUMERIC_CODE_LENGTH); //generate code of length 10

        } while (bookingReferenceRepository.existsByReferenceNo(bookingReference));

        // save the generated unique booking reference code
        saveBookingReference(bookingReference);

        return bookingReference;
    }

    private String generateRandomAlphanumericCode(int length) {
        SecureRandom random = new SecureRandom();

        return IntStream.range(0, length)
                .map(i -> random.nextInt(UPPER_ALPHANUMERIC_CHARS.length()))         // generate random index
                .mapToObj(index -> String.valueOf(UPPER_ALPHANUMERIC_CHARS.charAt(index)))          // convert index to character
                .collect(Collectors.joining());
    }

    private void saveBookingReference(String referenceNo) {
        BookingReference bookingReference = new BookingReference(referenceNo);
        bookingReferenceRepository.save(bookingReference);
    }

}
