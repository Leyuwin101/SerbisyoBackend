package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.ProviderDocument;
import com.example.serbisyofullstack.model.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProviderDocumentRepository extends JpaRepository<ProviderDocument, Long> {

    List<ProviderDocument> findByProviderId(Long providerId);

    List<ProviderDocument> findByStatus(DocumentStatus status);
}
