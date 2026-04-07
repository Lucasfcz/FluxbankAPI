package io.github.Lucasfcz.fluxbank.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Transfer request DTO for inter-account transfers.
 * Both source and destination accounts must exist and be active.
 */
@Schema(description = "Transfer request between two accounts")
public record TransferRequestDTO(
        @Schema(description = "Source account ID (account that sends funds)", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "Source account ID is required")
        UUID fromId,

        @Schema(description = "Destination account ID (account that receives funds)", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotNull(message = "Destination account ID is required")
        UUID toId,

        @Schema(description = "Transfer amount (must be positive)", example = "250.75")
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount
) {}


