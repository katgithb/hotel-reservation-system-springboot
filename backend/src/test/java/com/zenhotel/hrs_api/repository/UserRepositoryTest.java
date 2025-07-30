package com.zenhotel.hrs_api.repository;

import com.github.javafaker.Faker;
import com.zenhotel.hrs_api.TestcontainersConfig;
import com.zenhotel.hrs_api.entity.Role;
import com.zenhotel.hrs_api.entity.User;
import com.zenhotel.hrs_api.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    private final Faker faker = new Faker();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private String email;
    private String firstName;
    private String lastName;
    private Role customerRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        userRepository.deleteAll();

        // Generate test data using Faker
        email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();

        // Create and persist roles for testing
        customerRole = roleRepository.findByRoleName(RoleType.CUSTOMER.name())
                .orElseGet(() -> roleRepository.save(Role.of(RoleType.CUSTOMER)));
        adminRole = roleRepository.findByRoleName(RoleType.ADMIN.name())
                .orElseGet(() -> roleRepository.save(Role.of(RoleType.ADMIN)));
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void shouldReturnTrueWhenUserExistsByEmail() {
        // Given
        User user = createTestUser(email, firstName, lastName, customerRole);
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsUserByEmail(email);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        // Given
        String email = "nonexistent@example.com";

        // When
        boolean exists = userRepository.existsUserByEmail(email);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find user by email when user exists")
    void shouldFindUserByEmailWhenUserExists() {
        // Given
        User user = createTestUser(email, firstName, lastName, adminRole);
        User savedUser = userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(savedUser);
    }

    @Test
    @DisplayName("Should return empty optional when finding user by non-existent email")
    void shouldReturnEmptyOptionalWhenFindingUserByNonExistentEmail() {
        // Given
        String email = "nonexistent@example.com";

        // When
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should save and find user by email with only required fields (lastName null)")
    void shouldSaveAndFindUserByEmailWithOnlyRequiredFields() {
        // Given
        String phoneNumber = faker.phoneNumber().cellPhone();
        String password = "hashedPassword123";
        User user = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .phoneNumber(phoneNumber)
                .role(customerRole)
                .isActive(true)
                .build();

        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(email);
        assertThat(foundUser.get().getPassword()).isEqualTo(password);
        assertThat(foundUser.get().getFirstName()).isEqualTo(firstName);
        assertThat(foundUser.get().getLastName()).isNull();
        assertThat(foundUser.get().getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(foundUser.get().getRole()).isEqualTo(customerRole);
        assertThat(foundUser.get().getIsActive()).isTrue();
    }

    private User createTestUser(String email, String firstName, String lastName, Role role) {
        return User.builder()
                .email(email)
                .password("hashedPassword")
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber("+12345678901")
                .role(role)
                .isActive(true)
                .build();
    }

}