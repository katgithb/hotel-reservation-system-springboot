package com.zenhotel.hrs_api.payload;

import com.zenhotel.hrs_api.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = "FirstName is required")
    private String firstName;

    private String lastName; //optional

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Phone Number is required")
    @Size(max = 15)
    private String phoneNumber;

    private RoleType role; //optional

    @NotBlank(message = "Password is required")
    private String password;

}
