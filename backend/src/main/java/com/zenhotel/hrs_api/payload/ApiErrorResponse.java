package com.zenhotel.hrs_api.payload;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        String path,
        int status,
        String message,
        String errorCode,
        OffsetDateTime timestamp
) {
    
    public ApiErrorResponse(String path, int status, String message) {
        this(path, status, message, "INTERNAL_SERVER_ERROR", OffsetDateTime.now());
    }

    public ApiErrorResponse(String path, int status, String message, String errorCode) {
        // Calls the canonical constructor with current date and time for timestamp
        this(path, status, message, errorCode, OffsetDateTime.now());
    }

}
