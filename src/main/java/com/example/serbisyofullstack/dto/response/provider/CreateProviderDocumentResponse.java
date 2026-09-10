package com.example.serbisyofullstack.dto.response.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ProviderDocumentDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for a document upload. {@link ProviderDocumentDto} carries the
 * generated id, the document type, and its initial (PENDING) review status; the
 * raw file is never echoed back.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateProviderDocumentResponse {

    @Schema(description = "The stored document metadata, without file contents.")
    private ProviderDocumentDto document;

    @Schema(description = "Server timestamp of the upload.")
    private LocalDateTime uploadedAt;
}
