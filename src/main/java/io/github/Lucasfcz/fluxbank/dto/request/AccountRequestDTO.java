package io.github.Lucasfcz.fluxbank.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Transaction request for deposit and withdrawal operations")
public record AccountRequestDTO(
        @Schema(description = "Target account ID for the operation", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "Account ID is required")
        UUID accountId,

        @Schema(description = "Transaction amount (must be positive)", example = "100.50")
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount
) {}


