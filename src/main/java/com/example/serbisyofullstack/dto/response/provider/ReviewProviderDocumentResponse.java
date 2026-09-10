package com.example.serbisyofullstack.dto.response.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ProviderDocumentDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after an admin approves or rejects a provider document. Returns
 * the updated document including who reviewed it and when.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReviewProviderDocumentResponse {

    @Schema(description = "The document with its new review status, reviewer, and review time.")
    private ProviderDocumentDto document;

    @Schema(description = "Server timestamp of the review decision.")
    private LocalDateTime reviewedAt;
}
