package com.example.redditspringangular.repository;

import com.example.redditspringangular.model.EnumRole;
import com.example.redditspringangular.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(EnumRole role);
}
