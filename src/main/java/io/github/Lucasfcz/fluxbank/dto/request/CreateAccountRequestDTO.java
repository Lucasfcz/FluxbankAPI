package io.github.Lucasfcz.fluxbank.dto.request;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequestDTO(
        @NotBlank
        String holderName, // Legal name of the account holder.

        @NotBlank
        String cpf, // Brazilian individual taxpayer registry id.

        @Email
        @NotBlank
        String email, // Unique contact email for the account.

        @NotNull
        AccountType accountType // Product type selected for the account.
) {}
