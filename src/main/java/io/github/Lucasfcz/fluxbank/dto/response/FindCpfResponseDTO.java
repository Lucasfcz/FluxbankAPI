package io.github.Lucasfcz.fluxbank.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Response containing the user's CPF")
public record FindCpfResponseDTO(
        @Schema(description = "CPF associated with the account")
        String cpf,
        @Schema(description = "balance of account")
        BigDecimal balance){
}
