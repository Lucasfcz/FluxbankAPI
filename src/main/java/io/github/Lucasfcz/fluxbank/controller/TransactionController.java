package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.dto.request.AccountRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.request.TransferRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.response.AccountResponseDTO;
import io.github.Lucasfcz.fluxbank.mapper.AccountMapper;
import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Banking Operations Controller (Transactions).
 *
 * Provides endpoints to perform financial operations:
 * - POST /transactions/deposit: Deposit money to an account
 * - POST /transactions/withdraw: Withdraw money from an account
 * - POST /transactions/transfer: Transfer money between accounts
 *
 * All operations require JWT authentication and use optimistic concurrency control
 * to ensure data integrity in high-concurrency scenarios.
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(
    name = "Banking Transactions",
    description = "Financial operations - deposits, withdrawals and transfers between accounts. Requires JWT authentication. " +
                  "Uses Optimistic Locking to detect and prevent concurrent update conflicts (HTTP 409)."
)
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountMapper accountMapper;

    @Operation(
        summary = "Deposit money into an account",
        description = "Performs a deposit into a specific account. The account must be active and the amount must be positive. " +
                      "Returns the updated account information with the new balance."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Deposit successfully completed",
            content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request - amount must be greater than zero or account is inactive"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found in the system"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - concurrent update detected. Please retry the operation"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - authentication required"
        )
    })
    @PostMapping("/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(@RequestBody @Valid AccountRequestDTO request) {
        Account account = transactionService.deposit(request.accountId(), request.amount());
        return ResponseEntity.ok(accountMapper.toAccountResponseDTO(account));
    }

    @Operation(
        summary = "Withdraw money from an account",
        description = "Performs a withdrawal from a specific account. The account must be active, have sufficient balance, and the amount must be positive. " +
                      "Returns the updated account information with the new balance."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Withdrawal successfully completed",
            content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request - insufficient balance, amount must be positive, or account is inactive"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found in the system"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - concurrent update detected. Please retry the operation"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - authentication required"
        )
    })
    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(@RequestBody @Valid AccountRequestDTO request) {
        Account account = transactionService.withdraw(request.accountId(),request.amount());
        return ResponseEntity.ok(accountMapper.toAccountResponseDTO(account));
    }

    @Operation(
        summary = "Transfer money between two accounts",
        description = "Transfers money from one account to another. Both accounts must be active, exist in the system, and be different. " +
                      "The source account must have sufficient balance. The operation is atomic - either both accounts are updated successfully " +
                      "or both remain unchanged if an error occurs."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Transfer successfully completed",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"Transfer successful\"}"))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request - source and destination must be different, insufficient balance, or accounts are inactive"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "One or both accounts not found in the system"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - concurrent update detected. Please retry the operation"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - authentication required"
        )
    })
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody @Valid TransferRequestDTO request) {
        transactionService.transfer(request.fromId(), request.toId(), request.amount());
        return ResponseEntity.ok("Transfer successful");
    }
}
