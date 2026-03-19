package io.github.Lucasfcz.fluxbank.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Response containing the user's email")
public record FindEmailResponseDTO(
        @Schema(description = "email associated with the account")
        String email,
        @Schema(description = "balance of account")
        BigDecimal balance
) {}
