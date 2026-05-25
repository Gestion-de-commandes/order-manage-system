package com.mauth.repository;

import com.mauth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // SELECT * FROM roles WHERE name = ?
    // Exemple : roleRepository.findByName("ROLE_USER")
    Optional<Role> findByName(String name);
}