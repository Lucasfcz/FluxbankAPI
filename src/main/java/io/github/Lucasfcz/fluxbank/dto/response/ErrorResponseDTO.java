package io.github.Lucasfcz.fluxbank.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Standard error response DTO.
 * Returned by GlobalExceptionHandler for all exceptional scenarios.
 * Ensures consistent error format across all API endpoints.
 */
@Schema(description = "Standard error response")
public record ErrorResponseDTO(
        @Schema(description = "Server timestamp when the error occurred", example = "2024-01-15T10:30:00")
        LocalDateTime timestamp,

        @Schema(description = "Human-readable error description", example = "Account not found")
        String message
) {}


