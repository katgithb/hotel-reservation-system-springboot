package com.zenhotel.hrs_api.enums;

import com.zenhotel.hrs_api.exception.RequestValidationException;

import java.util.Arrays;

public enum RoomType {
    SINGLE,
    DOUBLE,
    SUITE,
    TRIPLE;

    public static RoomType from(String value) {

        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RequestValidationException("Invalid room type. " + "Valid values are: " +
                        Arrays.toString(values()) + " (case-insensitive)."));
    }

}
