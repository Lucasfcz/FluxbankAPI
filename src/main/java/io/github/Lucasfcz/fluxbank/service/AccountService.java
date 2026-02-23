package io.github.Lucasfcz.fluxbank.service;

import io.github.Lucasfcz.fluxbank.domain.Account;
import io.github.Lucasfcz.fluxbank.domain.AccountType;
import io.github.Lucasfcz.fluxbank.exception.IdNotFoundException;
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

        Account account = new Account(holderName, cpf, email, accountType);

        return repository.save(account);
    }

    @Transactional
    public Account deposit(UUID accountId, BigDecimal amount) {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new IdNotFoundException("Account Id not found"));
        account.deposit(amount);

        return account;
    }

    @Transactional
    public Account withdraw(UUID accountId, BigDecimal amount) {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new IdNotFoundException("Account Id not found"));
        account.withdraw(amount);

        return account;
    }

    @Transactional
    public void transfer(UUID fromId, UUID toId, BigDecimal amount) {

        if (fromId.equals(toId)) {
            throw new SameAccountException("Cannot transfer to the same account");
        }

        Account fromAccount = repository.findById(fromId)
                .orElseThrow(() -> new IdNotFoundException("Source account not found"));

        Account toAccount = repository.findById(toId)
                .orElseThrow(() -> new IdNotFoundException("Destination account not found"));

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

    }
}
