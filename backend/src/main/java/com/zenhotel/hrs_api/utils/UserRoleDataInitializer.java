package com.zenhotel.hrs_api.utils;

import com.zenhotel.hrs_api.entity.Role;
import com.zenhotel.hrs_api.entity.User;
import com.zenhotel.hrs_api.enums.RoleType;
import com.zenhotel.hrs_api.repository.RoleRepository;
import com.zenhotel.hrs_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRoleDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.phone-number}")
    private String adminPhoneNumber;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting user role data initialization...");

        // 1. Initialize Roles
        createRoleIfNotFound(RoleType.CUSTOMER.name());
        Role adminRole = createRoleIfNotFound(RoleType.ADMIN.name());

        // 2. Initialize Admin User
        createAdminUserIfNotFound(adminRole);

        log.info("User role data initialization complete.");
    }

    private Role createRoleIfNotFound(String roleName) {
        Optional<Role> existingRole = roleRepository.findByRoleName(roleName);

        if (existingRole.isPresent()) {
            log.info("Role '{}' already exists.", roleName);
            return existingRole.get();
        } else {
            Role newRole = new Role(roleName);
            roleRepository.save(newRole);

            log.info("Role '{}' created.", roleName);
            return newRole;
        }
    }

    private void createAdminUserIfNotFound(Role adminRole) {
        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                user -> log.info("Admin user with email '{}' already exists.", adminEmail),
                () -> {
                    User adminUser = User.builder()
                            .firstName("Admin")
                            .lastName("")
                            .email(adminEmail)
                            .password(passwordEncoder.encode(adminPassword)) // Hash the password
                            .phoneNumber(adminPhoneNumber)
                            .role(adminRole) // Assign the ADMIN role
                            .isActive(true)
                            .build();

                    userRepository.save(adminUser);
                    log.info("Admin user with email '{}' created successfully.", adminEmail);
                }
        );
    }

}
