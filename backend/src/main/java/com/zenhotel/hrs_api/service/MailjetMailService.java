package com.zenhotel.hrs_api.service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TrackOpens;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import com.zenhotel.hrs_api.exception.EmailDeliveryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailjetMailService {

    private final MailjetClient mailjetClient;

    @Value("${mailjet.sender.email}")
    private String senderEmail;

    @Value("${mailjet.sender.name}")
    private String senderName;

    public void sendTransactionalEmail(String recipientEmail, String recipientName,
                                       String subject, String htmlContent) {

        try {
            TransactionalEmail message = TransactionalEmail
                    .builder()
                    .to(new SendContact(recipientEmail, recipientName)) // Recipient details
                    .from(new SendContact(senderEmail, senderName))     // Sender details
                    .htmlPart(htmlContent)
                    .subject(subject)
                    .trackOpens(TrackOpens.ENABLED)
                    .customID("zenhotel-hrs-mailjet-email")
                    .build();

            SendEmailsRequest request = SendEmailsRequest
                    .builder()
                    .message(message)
                    .build();

            SendEmailsResponse response = request.sendWith(mailjetClient);

            log.info("Mailjet response for text/html content: {}", response);

        } catch (MailjetException e) {
            log.error(e.getMessage());
            throw new EmailDeliveryException("An unexpected error occurred while sending email. Please try again.");
        }
    }

    public void sendTransactionalEmail(String recipientEmail, String recipientName,
                                       String subject, Properties params, Long templateId) {

        try {
            Map<String, Object> variablesMap = new HashMap<>();
            params.forEach((k, v) -> variablesMap.put((String) k, v));

            TransactionalEmail message = TransactionalEmail
                    .builder()
                    .to(new SendContact(recipientEmail, recipientName)) // Recipient details
                    .from(new SendContact(senderEmail, senderName))     // Sender details
                    .subject(subject)
                    .templateID(templateId)
                    .templateLanguage(true)
                    .variables(variablesMap)
                    .trackOpens(TrackOpens.ENABLED)
                    .customID("zenhotel-hrs-mailjet-email")
                    .build();

            SendEmailsRequest request = SendEmailsRequest
                    .builder()
                    .message(message)
                    .build();

            SendEmailsResponse response = request.sendWith(mailjetClient);

            log.info("Mailjet response for email template: {}", response);

        } catch (MailjetException e) {
            log.error(e.getMessage());
            throw new EmailDeliveryException("An unexpected error occurred while sending email. Please try again.");
        }
    }

}
