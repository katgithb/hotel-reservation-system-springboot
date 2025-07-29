package com.zenhotel.hrs_api.controller;

import com.zenhotel.hrs_api.payload.*;
import com.zenhotel.hrs_api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User Management Endpoints")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/account/profile")
    public ResponseEntity<ApiResponse> getOwnAccountDetails(
            Authentication authentication) {

        return ResponseEntity.ok(userService.getOwnAccountDetails(authentication));
    }

    @PutMapping("/account/update")
    public ResponseEntity<ApiResponse> updateOwnAccount(
            @RequestBody UserDTO userDTO) {

        return ResponseEntity.ok(userService.updateOwnAccount(userDTO));
    }

    @DeleteMapping("/account/delete")
    public ResponseEntity<ApiResponse> deleteOwnAccount() {
        return ResponseEntity.ok(userService.deleteOwnAccount());
    }

    @GetMapping("/account/bookings")
    public ResponseEntity<PagedResponse<BookingDTO>> getOwnBookingHistory(
            @ModelAttribute PageRequestDTO pageRequest) {

        return ResponseEntity.ok(userService.getOwnBookingHistory(pageRequest));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PagedResponse<UserDTO>> getAllUsers(
            @ModelAttribute PageRequestDTO pageRequest) {

        return ResponseEntity.ok(userService.getAllUsers(pageRequest));
    }

}
