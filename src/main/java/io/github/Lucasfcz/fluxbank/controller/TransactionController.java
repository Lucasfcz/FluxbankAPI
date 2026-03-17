package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.dto.request.AccountRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.response.AccountResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.request.TransferRequestDTO;
import io.github.Lucasfcz.fluxbank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit") // POST is used because this endpoint changes server state.
    public ResponseEntity<AccountResponseDTO> deposit(@RequestBody @Valid AccountRequestDTO request) {
        // Execute deposit and return the updated balance.
        Account account = transactionService.deposit(request.accountId(), request.amount());

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(@RequestBody @Valid AccountRequestDTO request){
        // Execute withdrawal and return the updated balance.
        Account account = transactionService.withdraw(request.accountId(),request.amount());

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getBalance()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer") // POST is used because this endpoint changes server state.
    public ResponseEntity<String> transfer(@RequestBody @Valid TransferRequestDTO request) {

        // Perform a transactional transfer between two accounts.
        transactionService.transfer(request.fromId(), request.toId(), request.amount());

        // Return a simple success confirmation.
        return ResponseEntity.ok("Transfer successful");
    }
}
