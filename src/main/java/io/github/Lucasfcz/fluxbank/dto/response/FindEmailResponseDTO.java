package io.github.Lucasfcz.fluxbank.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Simplified account response DTO for email-based searches.
 * Returns only email and current balance.
 */
@Schema(description = "Response containing the user's email and account balance")
public record FindEmailResponseDTO(
        @Schema(description = "Email associated with the account", example = "user@example.com")
        String email,

        @Schema(description = "Current account balance", example = "1500.50")
        BigDecimal balance
) {}


