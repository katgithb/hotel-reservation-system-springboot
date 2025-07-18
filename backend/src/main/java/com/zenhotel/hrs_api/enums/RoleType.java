package com.zenhotel.hrs_api.enums;

import com.zenhotel.hrs_api.exception.RequestValidationException;

import java.util.Arrays;

public enum RoleType {
    CUSTOMER,
    ADMIN;

    public static RoleType from(String value) {

        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RequestValidationException("Invalid role: [%s]".formatted(value)));
    }

}
