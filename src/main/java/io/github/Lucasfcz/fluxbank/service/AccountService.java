package io.github.Lucasfcz.fluxbank.service;

import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.IdNotFoundException;
import io.github.Lucasfcz.fluxbank.exception.ResourceConflictException;
import io.github.Lucasfcz.fluxbank.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final AuthService authService;

    @Transactional
    public Account createAccount(String holderName, String cpf, String email, AccountType accountType) {

        JwtUser owner = authService.getAuthenticatedUser();

        if (repository.findByCpf(cpf).isPresent()) {
            throw new ResourceConflictException("CPF is already registered");
        }

        if (repository.findByEmail(email).isPresent()) {
            throw new ResourceConflictException("Email is already registered");
        }

        Account account = new Account(owner, holderName, cpf, email, accountType);

        return repository.save(account);
    }

    //Find Account methods
    public Account findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IdNotFoundException("Account Id not found in system"));
    }

    public Account findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new IdNotFoundException("Email not found in system"));
    }

    public Account findByCpf(String cpf){
        return repository.findByCpf(cpf).orElseThrow(() -> new IdNotFoundException("Cpf not found in system"));
    }

    public List <Account> findAll(){
        return repository.findAll();
    }

    public Account updateAccount(UUID id, String holderName, String email, AccountType accountType) {
        Account account = repository.findById(id).orElseThrow(() -> new IdNotFoundException("Account Id not found"));

        if (email != null && !email.equals(account.getEmail())) {
            if (repository.findByEmail(email).isPresent())  {
                throw new ResourceConflictException("Email is already registered");
            }
            account.changeEmail(email);
        }
        if (holderName != null) {
            account.changeHolderName(holderName);
        }
        if (accountType != null) {
            account.changeAccountType(accountType);
        }

        return repository.save(account);
    }

    @Transactional
    public void deactivateAccount(UUID id) {
        Account account = findById(id);
        account.deactivate();
    }
}