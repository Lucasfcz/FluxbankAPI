package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.dto.response.FindCpfResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.FindEmailResponseDTO;
import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.model.Transaction;
import io.github.Lucasfcz.fluxbank.dto.response.AccountResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.request.CreateAccountRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.response.TransactionResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.request.UpdateAccountRequestDTO;
import io.github.Lucasfcz.fluxbank.service.AccountService;
import io.github.Lucasfcz.fluxbank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;
    private final TransactionService transactionService;

    @Operation(summary = "Create account")
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody @Valid CreateAccountRequestDTO request) {
        // Delegate account creation to the service layer.
        Account account = service.createAccount(
                request.holderName(),
                request.cpf(),
                request.email(),
                request.accountType()
        );

        // Return only public account data to the client.
        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );

        return ResponseEntity.status(201).body(response);
    }

    //Find account methods(Id, Email and Cpf)
    @Operation(summary = "Find account by id")
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> findById(@PathVariable UUID accountId) {

        Account account = service.findById(accountId);

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Find account by email")
    @GetMapping("/email/{email}")
    public ResponseEntity<FindEmailResponseDTO> findByEmail(@PathVariable String email) {

        Account account = service.findByEmail(email);

        FindEmailResponseDTO response = new FindEmailResponseDTO(
                account.getEmail(),
                account.getBalance()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Find account by cpf")
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<FindCpfResponseDTO> findByCpf(@PathVariable String cpf) {

        Account account = service.findByCpf(cpf);

        FindCpfResponseDTO response = new FindCpfResponseDTO(
                account.getCpf(),
                account.getBalance()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Inactive an account")
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deactivateAccount(@PathVariable UUID accountId) {
        service.deactivateAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Find all accounts in system")
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> findAll(){
        List<Account> accounts = service.findAll();

        List<AccountResponseDTO> response = accounts.stream()
                .map(account -> new AccountResponseDTO(
                        account.getId(),
                        account.getBalance()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List transactions(deposits/withdraws/transfers) of account")
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(
            @PathVariable UUID accountId,
            Pageable pageable
    ) {
        Page<Transaction> transactions = transactionService.getAccountTransactions(accountId, pageable);
        Page<TransactionResponseDTO> response = transactions.map(transaction -> new TransactionResponseDTO(
                transaction.getId(),
                transaction.getFromAccount().getId(),
                transaction.getToAccount().getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedAt()
        ));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update account information")
    @PatchMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable UUID accountId,
            @RequestBody @Valid UpdateAccountRequestDTO request) {

        Account account = service.updateAccount(
                accountId,
                request.holderName(),
                request.email(),
                request.accountType()
        );

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );

        return ResponseEntity.ok(response);
    }
}