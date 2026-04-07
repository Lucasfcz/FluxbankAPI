package io.github.Lucasfcz.fluxbank.dto.request;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Bank account creation request")
public record CreateAccountRequestDTO(
        @Schema(description = "Name of the account holder", example = "John Doe")
        @NotBlank(message = "Holder name is required")
        String holderName,

        @Schema(description = "11 digits, must be unique", example = "12345678900")
        @NotBlank(message = "CPF is required")
        String cpf,

        @Schema(description = "Email address - must be unique and valid", example = "john@example.com")
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @Schema(description = "Account type (CHECKING, SAVINGS, BUSINESS, INVESTMENT, DIGITAL_WALLET)", example = "CHECKING")
        @NotNull(message = "Account type is required")
        AccountType accountType
) {}


