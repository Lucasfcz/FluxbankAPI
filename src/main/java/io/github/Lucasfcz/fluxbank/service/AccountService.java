package io.github.Lucasfcz.fluxbank.service;

import io.github.Lucasfcz.fluxbank.domain.Account;
import io.github.Lucasfcz.fluxbank.domain.AccountType;
import io.github.Lucasfcz.fluxbank.exception.IdNotFoundException;
import io.github.Lucasfcz.fluxbank.exception.ResourceConflictException;
import io.github.Lucasfcz.fluxbank.exception.SameAccountException;
import io.github.Lucasfcz.fluxbank.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    @Transactional
    public Account createAccount(String holderName, String cpf, String email, AccountType accountType) {
        if (repository.findByCpf(cpf).isPresent()) {
            throw new ResourceConflictException("CPF is already registered");
        }

        if (repository.findByEmail(email).isPresent()) {
            throw new ResourceConflictException("Email is already registered");
        }

        Account account = new Account(holderName, cpf, email, accountType);

        return repository.save(account);
    }

    @Transactional
    public Account deposit(UUID accountId, BigDecimal amount) {
        // Load the account or fail fast when the id does not exist.
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new IdNotFoundException("Account Id not found"));
        account.deposit(amount);

        // Dirty checking persists balance changes at transaction commit.
        return account;
    }

    @Transactional
    public Account withdraw(UUID accountId, BigDecimal amount) {
        // Load the account or fail fast when the id does not exist.
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new IdNotFoundException("Account Id not found"));
        account.withdraw(amount);

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

    }
}
