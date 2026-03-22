package io.github.Lucasfcz.fluxbank.service;

import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.model.Transaction;
import io.github.Lucasfcz.fluxbank.enums.TransactionType;
import io.github.Lucasfcz.fluxbank.exception.IdNotFoundException;
import io.github.Lucasfcz.fluxbank.exception.SameAccountException;
import io.github.Lucasfcz.fluxbank.repository.AccountRepository;
import io.github.Lucasfcz.fluxbank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository repository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Account deposit(UUID accountId, BigDecimal amount) {
        // Load the account or fail fast when the id does not exist.
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new IdNotFoundException("Account Id not found"));
        account.deposit(amount);
        transactionRepository.save(new Transaction(null, account, amount, TransactionType.DEPOSIT));

        // Dirty checking persists balance changes at transaction commit.
        return account;
    }

    @Transactional
    public Account withdraw(UUID accountId, BigDecimal amount) {
        // Load the account or fail fast when the id does not exist.
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new IdNotFoundException("Account Id not found"));
        account.withdraw(amount);
        transactionRepository.save(new Transaction(account, null, amount, TransactionType.WITHDRAW));

        // Dirty checking persists balance changes at transaction commit.
        return account;
    }

    @Transactional
    public void transfer(UUID fromId, UUID toId, BigDecimal amount) {

        // Prevent self-transfer to avoid meaningless operations.
        if (fromId.equals(toId)) {
            throw new SameAccountException("Cannot transfer to the same account");
        }

        Account fromAccount = repository.findById(fromId)
                .orElseThrow(() -> new IdNotFoundException("Source account not found"));

        Account toAccount = repository.findById(toId)
                .orElseThrow(() -> new IdNotFoundException("Destination account not found"));

        // Keep the operation atomic: either both updates succeed or both rollback.
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
        transactionRepository.save(new Transaction(fromAccount, toAccount, amount, TransactionType.TRANSFER));

    }

    @Transactional
    public Page<Transaction> getAccountTransactions(UUID accountId, Pageable pageable) {
        if (!repository.existsById(accountId)) {
            throw new IdNotFoundException("Account Id not found");
        }
        return transactionRepository.findTransactionsById(
                accountId,
                pageable
        );
    }
}