package com.zenhotel.hrs_api.payload;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private int status;
    private String message;
    private Map<String, Object> payload;
    private final OffsetDateTime timestamp = OffsetDateTime.now();

    @JsonAnyGetter
    public Map<String, Object> getPayload() {
        return payload;
    }

}
