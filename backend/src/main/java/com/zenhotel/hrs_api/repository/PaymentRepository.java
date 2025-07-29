package com.zenhotel.hrs_api.repository;

import com.zenhotel.hrs_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
