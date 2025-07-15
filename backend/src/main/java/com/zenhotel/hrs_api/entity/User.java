package com.zenhotel.hrs_api.entity;


import com.zenhotel.hrs_api.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "First Name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Phone Number is required")
    @Size(max = 15)
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role; //e.g CUSTOMER, ADMIN

    @NotNull
    private Boolean isActive;

    @NotNull
    @PastOrPresent
    private final OffsetDateTime createdAt = OffsetDateTime.now();

    public String getName() {
        return (lastName == null || lastName.isBlank())
                ? firstName
                : firstName + " " + lastName;
    }

}
