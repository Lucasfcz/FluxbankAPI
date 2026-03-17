package io.github.Lucasfcz.fluxbank.dto.request;

import io.github.Lucasfcz.fluxbank.enums.AccountType;

public record UpdateAccountRequestDTO(
        String holderName,
        String email,
        AccountType accountType
) {}
