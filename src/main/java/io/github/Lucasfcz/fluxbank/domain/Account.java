package io.github.Lucasfcz.fluxbank.domain;

import io.github.Lucasfcz.fluxbank.exception.InsufficientBalanceException;
import io.github.Lucasfcz.fluxbank.exception.InvalidAmountException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_accounts")
@Getter
@NoArgsConstructor
public class Account {
    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String holderName;
    @Column(nullable = false,unique = true)
    private String cpf;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Version
    @Column(nullable = false)
    private Long version;

    public void deposit(BigDecimal amount){
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount){
        validateAmount(amount);
        validateSufficientBalance(amount);
        this.balance = this.balance.subtract(amount);
    }

    // Shared validation for deposit and withdrawal operations.
    private void validateAmount(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }

    // Domain rule: withdrawals cannot exceed available balance.
    private void validateSufficientBalance(BigDecimal amount){
        if (this.balance.compareTo(amount) < 0){
            throw new InsufficientBalanceException("The amount must be less than balance");
        }
    }
    // Constructor
    public Account(String holderName, String cpf, String email, AccountType accountType) {
        this.id = UUID.randomUUID();
        this.holderName = holderName;
        this.cpf = cpf;
        this.email = email;
        this.accountType = accountType;
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.version = 0L;
    }
}
