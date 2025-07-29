package com.zenhotel.hrs_api.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailjetConfig {

    @Value("${mailjet.api.key.public}")
    private String mailjetApiKeyPublic;

    @Value("${mailjet.api.key.private}")
    private String mailjetApiKeyPrivate;

    @Bean
    public MailjetClient mailjetClient() {
        ClientOptions options = ClientOptions.builder()
                .apiKey(mailjetApiKeyPublic)
                .apiSecretKey(mailjetApiKeyPrivate)
                .build();

        return new MailjetClient(options);
    }

}
