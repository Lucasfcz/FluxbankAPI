package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.dto.request.CreateAccountRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.request.UpdateAccountRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.response.AccountResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.FindCpfResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.FindEmailResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.TransactionResponseDTO;
import io.github.Lucasfcz.fluxbank.mapper.AccountMapper;
import io.github.Lucasfcz.fluxbank.mapper.TransactionMapper;
import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import io.github.Lucasfcz.fluxbank.model.Transaction;
import io.github.Lucasfcz.fluxbank.service.AccountService;
import io.github.Lucasfcz.fluxbank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Provides endpoints to create, consult, update and manage bank accounts.
 * All endpoints require JWT authentication (Bearer token).
 *
 * Supported operations:
 * -POST /accounts: Create new account
 * -GET /accounts: List all accounts
 * -GET /accounts/{id}: Find account by ID
 * -GET /accounts/email/{email}: Find account by email
 * -GET /accounts/cpf/{cpf}: Find account by CPF
 * -GET /accounts/{id}/transactions: List account transactions (paginated)
 * -PATCH /accounts/{id}: Update account information
 * -DELETE /accounts/{id}: Deactivate account (soft-delete)
 */
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(
    name = "Bank Accounts",
    description = "Bank account management - create, consult, update and list. Requires JWT authentication."
)
public class AccountController {

    private final AccountService service;
    private final TransactionService transactionService;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    @Operation(
        summary = "Create new bank account",
        description = "Creates a new bank account with the provided data. CPF and email must be unique in the system."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Account created successfully",
            content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request (validation failed or incomplete data)"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - CPF or email already registered"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated - invalid or expired JWT token"
        )
    })
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody @Valid CreateAccountRequestDTO request) {
        // Get authenticated JwtUser from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUser owner = (JwtUser) authentication.getPrincipal();

        Account account = service.createAccount(
                request.holderName(),
                request.cpf(),
                request.email(),
                request.accountType()
        );
        return ResponseEntity.status(201).body(accountMapper.toAccountResponseDTO(account));
    }

    @Operation(
        summary = "Find account by ID",
        description = "Returns the complete details of an account by its unique identifier (UUID)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Account found and returned successfully",
            content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        )
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> findById(
        @Parameter(
            description = "Unique account ID (UUID)",
            example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @PathVariable UUID accountId) {
        Account account = service.findById(accountId);
        return ResponseEntity.ok(accountMapper.toAccountResponseDTO(account));
    }

    @Operation(
        summary = "Find account by email",
        description = "Locates an account using the unique email address associated with the account. Returns ID and balance."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Account found",
            content = @Content(schema = @Schema(implementation = FindEmailResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Email not found in the system"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        )
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<FindEmailResponseDTO> findByEmail(
        @Parameter(
            description = "Unique account email",
            example = "lucas@example.com"
        )
        @PathVariable String email) {
        Account account = service.findByEmail(email);
        return ResponseEntity.ok(accountMapper.toFindEmailResponseDTO(account));
    }

    @Operation(
        summary = "Find account by CPF",
        description = "Locates an account using the unique CPF (Individual Taxpayer Registry Number) of the holder. Returns CPF and balance."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Account found",
            content = @Content(schema = @Schema(implementation = FindCpfResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "CPF not found in the system"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        )
    })
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<FindCpfResponseDTO> findByCpf(
        @Parameter(
            description = "CPF of the holder (11 digits)",
            example = "12345678900"
        )
        @PathVariable String cpf) {
        Account account = service.findByCpf(cpf);
        return ResponseEntity.ok(accountMapper.toFindCpfResponseDTO(account));
    }

    @Operation(
        summary = "List all accounts",
        description = "Returns a list with all accounts registered in the system. ⚠️ No pagination - use with caution in production with many records."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of accounts returned successfully",
            content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        )
    })
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> findAll() {
        List<Account> accounts = service.findAll();
        return ResponseEntity.ok(accounts.stream()
                .map(accountMapper::toAccountResponseDTO)
                .toList());
    }

    @Operation(
        summary = "List account transactions (paginated)",
        description = "Returns the paginated history of transactions (deposits, withdrawals and transfers) for a specific account. Includes filtering by type and sorting."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Transactions returned successfully",
            content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        )
    })
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(
            @Parameter(
                description = "Account ID",
                example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID accountId,
            @Parameter(
                description = "Pagination parameters. Examples: page=0&size=10&sort=createdAt,desc",
                example = "page=0&size=20"
            )
            Pageable pageable
    ) {
        Page<Transaction> transactions = transactionService.getAccountTransactions(accountId, pageable);
        return ResponseEntity.ok(transactions.map(transactionMapper::toTransactionResponseDTO));
    }

    @Operation(
        summary = "Update account information",
        description = "Updates one or more account fields (holder name, email, account type). Email must be unique if changed."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Account updated successfully",
            content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already registered by another account"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        )
    })
    @PatchMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @Parameter(
                description = "Account ID to update",
                example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID accountId,
            @RequestBody @Valid UpdateAccountRequestDTO request) {
        Account account = service.updateAccount(
                accountId,
                request.holderName(),
                request.email(),
                request.accountType()
        );
        return ResponseEntity.ok(accountMapper.toAccountResponseDTO(account));
    }

    @Operation(
        summary = "Deactivate account (soft-delete)",
        description = "Marks an account as inactive. Future operations will be rejected, but transaction history is preserved. Does not remove data from the database."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Account deactivated successfully (no content in response)"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        )
    })
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deactivateAccount(
            @Parameter(
                description = "Account ID to deactivate",
                example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID accountId) {
        service.deactivateAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
