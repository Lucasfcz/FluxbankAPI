package io.github.Lucasfcz.fluxbank.dto;

import io.github.Lucasfcz.fluxbank.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDTO(
        UUID transactionId,
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        TransactionType type,
        LocalDateTime createdAt
) {}
