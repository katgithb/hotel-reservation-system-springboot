package com.zenhotel.hrs_api.utils;

import com.zenhotel.hrs_api.entity.User;
import com.zenhotel.hrs_api.payload.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class UserUpdaterUtil {

    private final PasswordEncoder passwordEncoder;

    private <T> void setValueIfPresent(Supplier<T> getter, Consumer<T> setter) {
        Optional.ofNullable(getter.get()).ifPresent(setter);
    }

    public User updateUserDetailsFromDTO(UserDTO userDTO, User existingUser) {
        setValueIfPresent(userDTO::getEmail, existingUser::setEmail);
        setValueIfPresent(userDTO::getFirstName, existingUser::setFirstName);
        setValueIfPresent(userDTO::getLastName, existingUser::setLastName);
        setValueIfPresent(userDTO::getPhoneNumber, existingUser::setPhoneNumber);

        Optional.ofNullable(userDTO.getPassword()).ifPresent(
                password -> existingUser.setPassword(passwordEncoder.encode(password)));

        return existingUser;
    }

}
