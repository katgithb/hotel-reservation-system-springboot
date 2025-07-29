package com.zenhotel.hrs_api.repository;

import com.zenhotel.hrs_api.entity.BookingReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingReferenceRepository extends JpaRepository<BookingReference, Long> {

    boolean existsByReferenceNo(String referenceNo);

    Optional<BookingReference> findByReferenceNo(String referenceNo);
}
