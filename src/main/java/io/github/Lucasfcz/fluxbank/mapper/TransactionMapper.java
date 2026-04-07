package io.github.Lucasfcz.fluxbank.mapper;

import io.github.Lucasfcz.fluxbank.dto.response.TransactionResponseDTO;
import io.github.Lucasfcz.fluxbank.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponseDTO toTransactionResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getFromAccount() != null ? transaction.getFromAccount().getId() : null,
                transaction.getToAccount() != null ? transaction.getToAccount().getId() : null,
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedAt()
        );
    }
}

