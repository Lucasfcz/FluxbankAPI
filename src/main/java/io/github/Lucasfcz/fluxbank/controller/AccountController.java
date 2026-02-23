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
public class AccountController {

    private final AccountService service;

    @PostMapping("/create")
    public ResponseEntity<AccountResponseDTO> createAccount(
            @RequestBody @Valid CreateAccountRequestDTO request
    ) {
        Account account = service.createAccount(
                request.holderName(),
                request.cpf(),
                request.email(),
                request.accountType()
        );

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/deposit") // POST porque altera os dados
    public ResponseEntity<AccountResponseDTO> deposit(@RequestBody AccountRequestDTO request) {

        Account account = service.deposit(request.accountId(), request.amount());

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(@RequestBody AccountRequestDTO request){
        Account account = service.withdraw(request.accountId(),request.amount());

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer") // POST porque altera dados
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO request) {

        // Chama o service para realizar a transferência
        service.transfer(request.fromId(), request.toId(), request.amount());

        // Retorna uma mensagem de sucesso
        return ResponseEntity.ok("Transfer successful");
    }
}
