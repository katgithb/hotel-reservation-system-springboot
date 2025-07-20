package com.zenhotel.hrs_api.repository;

import com.zenhotel.hrs_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsUserByEmail(String email);

    Optional<User> findByEmail(String email);
}
