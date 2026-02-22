package io.github.Lucasfcz.fluxbank.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDTO(
        UUID accountId,
        BigDecimal balance
) {}