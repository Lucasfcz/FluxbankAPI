package io.github.Lucasfcz.fluxbank.dto;

import io.github.Lucasfcz.fluxbank.domain.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequestDTO(

        @NotBlank
        String holderName,

        @NotBlank
        String cpf,

        @Email
        @NotBlank
        String email,

        @NotNull
        AccountType accountType
) {}