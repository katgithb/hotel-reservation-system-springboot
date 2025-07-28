package com.zenhotel.hrs_api.enums;

import com.zenhotel.hrs_api.exception.RequestValidationException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UploadType {
    AVATAR("avatar"),
    ROOM("room");

    private final String uploadFolder;

    UploadType(String uploadFolder) {
        this.uploadFolder = uploadFolder;
    }

    public static UploadType from(String value) {
        if (value == null || value.isBlank()) {
            throw new RequestValidationException("Upload type is required");
        }

        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RequestValidationException("Invalid upload type: [%s]".formatted(value)));
    }

}
