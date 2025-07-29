package com.zenhotel.hrs_api.controller;

import com.zenhotel.hrs_api.payload.ApiResponse;
import com.zenhotel.hrs_api.payload.UserAuthRequest;
import com.zenhotel.hrs_api.payload.UserAuthResponse;
import com.zenhotel.hrs_api.payload.UserRegistrationRequest;
import com.zenhotel.hrs_api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication Endpoints")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(
            @RequestBody @Valid UserRegistrationRequest request) {

        return new ResponseEntity<>(userService.registerUser(request),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponse> loginUser(
            @RequestBody @Valid UserAuthRequest request) {

        return ResponseEntity.ok(userService.loginUser(request));
    }

}
