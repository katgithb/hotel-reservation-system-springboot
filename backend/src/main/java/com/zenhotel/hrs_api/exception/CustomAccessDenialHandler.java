package com.zenhotel.hrs_api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenhotel.hrs_api.payload.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDenialHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        HttpStatus status = HttpStatus.FORBIDDEN;

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                "Access denied. Insufficient permissions to access this resource.",
                "ACCESS_DENIED"
        );

        response.setContentType("application/json");
        response.setStatus(status.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
