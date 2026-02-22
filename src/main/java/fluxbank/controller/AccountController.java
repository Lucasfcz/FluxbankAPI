package fluxbank.controller;

import fluxbank.domain.model.Account;
import fluxbank.dto.AccountRequestDTO;
import fluxbank.dto.AccountResponseDTO;
import fluxbank.dto.TransferRequestDTO;
import fluxbank.service.AccountService;
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
