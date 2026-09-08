package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
