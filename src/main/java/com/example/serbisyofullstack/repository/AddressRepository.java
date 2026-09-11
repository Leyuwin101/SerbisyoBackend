package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByOwnerId(Long ownerId);

    Page<Address> findByOwnerId(Long ownerId, Pageable pageable);

    Optional<Address> findByIdAndOwnerId(Long id, Long ownerId);
}
