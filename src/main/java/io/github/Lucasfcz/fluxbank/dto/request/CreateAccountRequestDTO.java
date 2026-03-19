package io.github.Lucasfcz.fluxbank.dto.request;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Account creation")
public record CreateAccountRequestDTO(
        @Schema(description = "name of the holder of account")
        @NotBlank
        String holderName,
        @Schema(description = "holder account cpf")// Legal name of the account holder.
        @NotBlank
        String cpf, // Brazilian individual taxpa// yer registry id.
        @Schema(description = "email associated an account")
        @Email
        @NotBlank
        String email, // Unique contact email for the account.
        @Schema(description = "type of account(CHECKING,SAVINGS,BUSINESS,INVESTMENT,DIGITAL_WALLET")
        @NotNull
        AccountType accountType // Product type selected for the account.
) {}
