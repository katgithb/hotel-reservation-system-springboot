package com.zenhotel.hrs_api.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zenhotel.hrs_api.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDTO {

    private Long id;
    private Integer roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private Integer capacity;

    private String description; //additional data for the room
    private String imageUploadId;
    private String imageUrl; //this will hold the room picture
    private OffsetDateTime createdAt;
    
}
