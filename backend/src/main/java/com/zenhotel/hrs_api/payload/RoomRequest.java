package com.zenhotel.hrs_api.payload;

import com.zenhotel.hrs_api.enums.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {

    @NotNull
    @Min(value = 1, message = "Room Number must be at least 1")
    private Integer roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType type;

    @NotNull
    @DecimalMin(value = "0.1", message = "Price per night is required")
    private BigDecimal pricePerNight;

    @NotNull
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String description;

    private MultipartFile imageFile;

}
