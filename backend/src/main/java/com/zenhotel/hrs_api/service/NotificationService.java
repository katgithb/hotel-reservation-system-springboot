package com.zenhotel.hrs_api.service;

import com.zenhotel.hrs_api.payload.NotificationDTO;

import java.util.Properties;

public interface NotificationService {

    void sendEmail(NotificationDTO notificationDTO);

    void sendEmail(NotificationDTO notificationDTO, Properties params);

    void sendSms();

    void sendWhatsapp();
}
