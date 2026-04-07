package io.github.Lucasfcz.fluxbank.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Simplified account response DTO for CPF-based searches.
 * Returns only CPF and current balance.
 */
@Schema(description = "Response containing the user's CPF and account balance")
public record FindCpfResponseDTO(
        @Schema(description = "CPF associated with the account (11 digits)", example = "12345678900")
        String cpf,

        @Schema(description = "Current account balance", example = "1500.50")
        BigDecimal balance
) {}

