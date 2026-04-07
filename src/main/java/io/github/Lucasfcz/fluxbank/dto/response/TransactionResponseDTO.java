package io.github.Lucasfcz.fluxbank.dto.response;

import io.github.Lucasfcz.fluxbank.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Complete transaction information response DTO.
 * Represents financial operations (deposits, withdrawals, transfers).
 *
 * Field interpretations:
 * - DEPOSIT: fromAccountId is null (money enters the system)
 * - WITHDRAW: toAccountId is null (money exits the system)
 * - TRANSFER: both fromAccountId and toAccountId are populated
 */
@Schema(description = "Bank transaction information")
public record TransactionResponseDTO(
        @Schema(description = "Unique transaction identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID transactionId,

        @Nullable
        @Schema(description = "Source account ID (null for deposits)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID fromAccountId,

        @Nullable
        @Schema(description = "Destination account ID (null for withdrawals)", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID toAccountId,

        @Schema(description = "Transaction amount", example = "250.75")
        BigDecimal amount,

        @Schema(description = "Transaction type (DEPOSIT, WITHDRAW, TRANSFER)", example = "TRANSFER")
        TransactionType type,

        @Schema(description = "Transaction timestamp", example = "2024-01-15T14:30:00")
        LocalDateTime createdAt
) {}
