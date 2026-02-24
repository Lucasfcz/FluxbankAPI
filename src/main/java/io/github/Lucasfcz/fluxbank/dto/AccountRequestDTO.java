package io.github.Lucasfcz.fluxbank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequestDTO(
        @NotNull(message = "Account ID is required")
        UUID accountId, // Target account for the operation.
        @NotNull
        @Positive
        BigDecimal amount // Positive amount to be processed.
) {}
