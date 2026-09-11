package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByStatus(Status status);
}
