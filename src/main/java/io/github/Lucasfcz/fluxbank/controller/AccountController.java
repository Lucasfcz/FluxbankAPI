package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.domain.Account;
import io.github.Lucasfcz.fluxbank.dto.AccountRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.AccountResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.CreateAccountRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.TransferRequestDTO;
import io.github.Lucasfcz.fluxbank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
// Exposes account operations as HTTP endpoints.
public class AccountController {

    private final AccountService service;

    @PostMapping("/create")
    public ResponseEntity<AccountResponseDTO> createAccount(
            @RequestBody @Valid CreateAccountRequestDTO request
    ) {
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

    @PostMapping("/deposit") // POST is used because this endpoint changes server state.
    public ResponseEntity<AccountResponseDTO> deposit(@RequestBody @Valid AccountRequestDTO request) {
        // Execute deposit and return the updated balance.
        Account account = service.deposit(request.accountId(), request.amount());

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(@RequestBody @Valid AccountRequestDTO request){
        // Execute withdrawal and return the updated balance.
        Account account = service.withdraw(request.accountId(),request.amount());

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer") // POST is used because this endpoint changes server state.
    public ResponseEntity<String> transfer(@RequestBody @Valid TransferRequestDTO request) {

        // Perform a transactional transfer between two accounts.
        service.transfer(request.fromId(), request.toId(), request.amount());

        // Return a simple success confirmation.
        return ResponseEntity.ok("Transfer successful");
    }
}
