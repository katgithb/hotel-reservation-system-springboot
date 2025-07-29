package com.zenhotel.hrs_api.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zenhotel.hrs_api.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {

    private Long id;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Recipient email is required")
    private String recipientEmail;

    private String recipientName;
    private String body;

    private String bookingReference;
    private NotificationType type;
    private OffsetDateTime createdAt;

}
