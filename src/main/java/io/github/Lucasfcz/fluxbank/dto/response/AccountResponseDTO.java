package io.github.Lucasfcz.fluxbank.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDTO(
        UUID accountId, // Public account identifier.
        BigDecimal balance // Current balance after the operation.
) {}
