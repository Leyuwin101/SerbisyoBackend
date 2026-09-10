package com.example.serbisyofullstack.dto.request.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * Multipart request body for a provider uploading a verification document
 * (e.g. government ID, business permit). The stored {@code storageKey} and
 * initial {@code status} are set by the server, not the client.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateProviderDocumentRequest {
    @Schema(description = "Kind of document being submitted, e.g. \"GOVERNMENT_ID\", \"BUSINESS_PERMIT\".")
    @NotBlank(message = "Document type is required")
    @Size(max = 100, message = "Document type must not exceed 100 characters")
    private String type;

    @Schema(description = "The uploaded file itself; the server persists it and keeps only the storage key.")
    private MultipartFile file;

    @Schema(description = "Optional date after which the document (e.g. a permit) is no longer valid.")
    private LocalDate expiryDate;
}
