package com.zenhotel.hrs_api.service.impl;

import com.zenhotel.hrs_api.entity.Notification;
import com.zenhotel.hrs_api.enums.NotificationType;
import com.zenhotel.hrs_api.payload.NotificationDTO;
import com.zenhotel.hrs_api.repository.NotificationRepository;
import com.zenhotel.hrs_api.service.MailjetMailService;
import com.zenhotel.hrs_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MailjetMailService mailjetService;

    @Override
    public void sendEmail(NotificationDTO notificationDTO) {
        log.info("Sending email ...");

        mailjetService.sendTransactionalEmail(notificationDTO.getRecipientEmail(),
                notificationDTO.getRecipientName(),
                notificationDTO.getSubject(),
                notificationDTO.getBody());

        //SAVE TO DATABSE
        Notification notificationToSave = Notification.builder()
                .recipientEmail(notificationDTO.getRecipientEmail())
                .recipientName(notificationDTO.getRecipientName())
                .subject(notificationDTO.getSubject())
                .body(notificationDTO.getBody())
                .bookingReference(notificationDTO.getBookingReference())
                .type(NotificationType.EMAIL)
                .build();

        notificationRepository.save(notificationToSave);
    }

    @Override
    public void sendEmail(NotificationDTO notificationDTO, Properties params) {
        log.info("Sending email with params ...");

        mailjetService.sendTransactionalEmail(notificationDTO.getRecipientEmail(),
                notificationDTO.getRecipientName(),
                notificationDTO.getSubject(),
                params,
                params.getProperty("templateId") != null ? Long.parseLong(params.getProperty("templateId")) : 0L);

        //SAVE TO DATABSE
        Notification notificationToSave = Notification.builder()
                .recipientEmail(notificationDTO.getRecipientEmail())
                .recipientName(notificationDTO.getRecipientName())
                .subject(notificationDTO.getSubject())
                .body(notificationDTO.getBody())
                .bookingReference(notificationDTO.getBookingReference())
                .type(NotificationType.EMAIL)
                .build();

        notificationRepository.save(notificationToSave);
    }

    @Override
    public void sendSms() {
    }

    @Override
    public void sendWhatsapp() {
    }

}
