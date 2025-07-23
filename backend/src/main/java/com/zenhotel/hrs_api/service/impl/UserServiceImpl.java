package com.zenhotel.hrs_api.service.impl;

import com.zenhotel.hrs_api.entity.Booking;
import com.zenhotel.hrs_api.entity.Role;
import com.zenhotel.hrs_api.entity.User;
import com.zenhotel.hrs_api.enums.RoleType;
import com.zenhotel.hrs_api.exception.DuplicateResourceException;
import com.zenhotel.hrs_api.exception.ResourceNotFoundException;
import com.zenhotel.hrs_api.payload.*;
import com.zenhotel.hrs_api.repository.BookingRepository;
import com.zenhotel.hrs_api.repository.RoleRepository;
import com.zenhotel.hrs_api.repository.UserRepository;
import com.zenhotel.hrs_api.security.AuthUser;
import com.zenhotel.hrs_api.security.JwtUtil;
import com.zenhotel.hrs_api.service.UserService;
import com.zenhotel.hrs_api.utils.UserUpdaterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BookingRepository bookingRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserUpdaterUtil userUpdaterUtil;
    private final ModelMapper modelMapper;

    @Override
    public boolean existsUserWithEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }

    @Override
    public ApiResponse registerUser(UserRegistrationRequest registrationRequest) {
        RoleType roleType = RoleType.CUSTOMER;

        // Get user role from request if provided
        if (registrationRequest.getRole() != null) {
            roleType = registrationRequest.getRole();
        }

        // Check if email already exists
        String email = registrationRequest.getEmail();
        if (existsUserWithEmail(email)) {
            throw new DuplicateResourceException("This email is already taken");
        }

        // Get user role
        String roleName = roleType.name();
        Role userRole = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        User userToSave = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(userRole)
                .isActive(true)
                .build();

        userRepository.save(userToSave);

        return ApiResponse.builder()
                .status(201)
                .message("User registered successfully")
                .build();

    }

    @Override
    public UserAuthResponse loginUser(UserAuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
                        authRequest.getPassword())
        );

        AuthUser principal = (AuthUser) authentication.getPrincipal();
        User user = principal.getUser();

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleName());
        Date expirationDate = jwtUtil.getExpirationFromToken(token);
        String roleName = user.getRole().getRoleName();

        return UserAuthResponse.builder()
                .status(200)
                .message("user logged in successfully")
                .role(RoleType.from(roleName))
                .token(token)
                .isActive(user.getIsActive())
                .expirationTime(expirationDate.toInstant())
                .build();
    }

    @Override
    public PagedResponse<UserDTO> getAllUsers(PageRequestDTO pageRequest) {
        Pageable pageable = pageRequest.toPageable();
        Page<User> userPage = userRepository.findAll(pageable);

        return PagedResponse.fromPage(
                200,
                "success",
                userPage,
                modelMapper,
                new TypeToken<List<UserDTO>>() {
                });
    }

    @Override
    public ApiResponse getOwnAccountDetails(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        log.info("Inside getOwnAccountDetails, user email: {}", email);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return ApiResponse.builder()
                .status(200)
                .message("success")
                .payload(Map.of("user", userDTO))
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public ApiResponse updateOwnAccount(UserDTO userDTO) {
        User existingUser = getCurrentLoggedInUser();
        log.info("Inside updateOwnAccount, user email: {}", existingUser.getEmail());

        // Check if email already exists
        String email = userDTO.getEmail();
        if (existsUserWithEmail(email)) {
            throw new DuplicateResourceException("This email is already taken");
        }

        // Update user details from DTO
        User updatedUser = userUpdaterUtil.updateUserDetailsFromDTO(userDTO, existingUser);

        userRepository.save(updatedUser);

        return ApiResponse.builder()
                .status(200)
                .message("User updated successfully")
                .build();
    }

    @Override
    public ApiResponse deleteOwnAccount() {
        User user = getCurrentLoggedInUser();

        userRepository.delete(user);

        return ApiResponse.builder()
                .status(200)
                .message("User deleted successfully")
                .build();
    }

    @Override
    public PagedResponse<BookingDTO> getOwnBookingHistory(PageRequestDTO pageRequest) {
        User user = getCurrentLoggedInUser();
        Pageable pageable = pageRequest.toPageable();
        Page<Booking> bookingPage = bookingRepository.findByUserId(user.getId(), pageable);

        return PagedResponse.fromPage(
                200,
                "success",
                bookingPage,
                modelMapper,
                new TypeToken<List<BookingDTO>>() {
                });
    }

}
