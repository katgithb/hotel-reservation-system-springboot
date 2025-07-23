package com.zenhotel.hrs_api.service;

import com.zenhotel.hrs_api.entity.User;
import com.zenhotel.hrs_api.payload.*;
import org.springframework.security.core.Authentication;

public interface UserService {

    boolean existsUserWithEmail(String email);

    ApiResponse registerUser(UserRegistrationRequest registrationRequest);

    UserAuthResponse loginUser(UserAuthRequest authRequest);

    PagedResponse<UserDTO> getAllUsers(PageRequestDTO pageRequest);

    ApiResponse getOwnAccountDetails(Authentication authentication);

    User getCurrentLoggedInUser();

    ApiResponse updateOwnAccount(UserDTO userDTO);

    ApiResponse deleteOwnAccount();

    PagedResponse<BookingDTO> getOwnBookingHistory(PageRequestDTO pageRequest);
}
