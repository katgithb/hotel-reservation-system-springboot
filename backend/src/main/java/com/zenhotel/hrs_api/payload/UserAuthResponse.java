package com.zenhotel.hrs_api.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zenhotel.hrs_api.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAuthResponse {

    private int status;
    private String message;

    // for login response
    private String token;
    private RoleType role;
    private Boolean isActive;
    private Instant expirationTime;

    private final OffsetDateTime timestamp = OffsetDateTime.now();

}
