package io.github.Lucasfcz.fluxbank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDTO(
        @NotNull UUID fromId,
        @NotNull UUID toId,
        @NotNull @Positive BigDecimal amount
) {}