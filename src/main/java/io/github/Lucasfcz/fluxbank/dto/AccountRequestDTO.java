package io.github.Lucasfcz.fluxbank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequestDTO(
        @NotNull(message = "Account ID is required")
        UUID accountId,       // Qual conta vai receber o depósito
        @NotNull @Positive BigDecimal amount  // Quanto vai depositar (positivo)
) {}