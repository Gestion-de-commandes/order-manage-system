package com.mauth.repository;

import com.mauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data génère automatiquement :
    // SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT COUNT(*) > 0 FROM users WHERE username = ?
    Boolean existsByUsername(String username);

    // SELECT COUNT(*) > 0 FROM users WHERE email = ?
    Boolean existsByEmail(String email);
}