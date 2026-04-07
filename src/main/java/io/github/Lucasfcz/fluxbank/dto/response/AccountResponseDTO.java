package io.github.Lucasfcz.fluxbank.dto.response;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Complete account information response DTO.
 * Used in all account-related responses (create, find, update).
 */
@Schema(description = "Complete bank account information")
public record AccountResponseDTO(
        @Schema(description = "Unique account identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID accountId,
        
        @Schema(description = "Name of the account holder", example = "John Doe")
        String holderName,
        
        @Schema(description = "Account email address", example = "john@example.com")
        String email,
        
        @Schema(description = "Account type", example = "CHECKING")
        AccountType accountType,
        
        @Schema(description = "Current account balance", example = "1500.50")
        BigDecimal balance,
        
        @Schema(description = "Account active status", example = "true")
        boolean active,
        
        @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt
) {}
