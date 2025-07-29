package com.zenhotel.hrs_api.repository;

import com.zenhotel.hrs_api.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
