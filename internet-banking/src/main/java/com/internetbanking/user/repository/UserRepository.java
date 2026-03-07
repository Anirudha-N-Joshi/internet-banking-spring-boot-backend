package com.internetbanking.user.repository;

import com.internetbanking.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    boolean existsByUserName(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);
}
