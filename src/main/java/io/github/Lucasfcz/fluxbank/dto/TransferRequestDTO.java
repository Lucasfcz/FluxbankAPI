package io.github.Lucasfcz.fluxbank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDTO(
        @NotNull UUID fromId, // Account that sends the funds.
        @NotNull UUID toId,   // Account that receives the funds.
        @NotNull @Positive BigDecimal amount
) {}
