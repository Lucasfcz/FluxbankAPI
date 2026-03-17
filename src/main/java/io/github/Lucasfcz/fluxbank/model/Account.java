package io.github.Lucasfcz.fluxbank.model;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.AccountInactiveException;
import io.github.Lucasfcz.fluxbank.exception.InsufficientBalanceException;
import io.github.Lucasfcz.fluxbank.exception.InvalidAmountException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_accounts")
@Getter
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String holderName;

    @Column(nullable = false,unique = true)
    private String cpf;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column
    private boolean active = true;

    public void deposit(BigDecimal amount){
        validateActive();
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount){
        validateActive();
        validateAmount(amount);
        validateSufficientBalance(amount);
        this.balance = this.balance.subtract(amount);
    }

    // Shared validation for deposit and withdrawal operations
    private void validateAmount(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }

    // Domain rule: withdrawals cannot exceed available balance
    private void validateSufficientBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("The amount must be less than balance");
        }
    }

    private void validateActive() {
        if (!this.active) {
            throw new AccountInactiveException("Account is inactive");
        }
    }

    //Changer methods
    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeHolderName(String holderName) {
        this.holderName = holderName;
    }

    public void changeAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    // Deactivate the account instead of deleting it to preserve transaction history
    public void deactivate() {
        this.active = false;
    }

    // Constructor
    public Account(String holderName, String cpf, String email, AccountType accountType) {
        this.holderName = holderName;
        this.cpf = cpf;
        this.email = email;
        this.accountType = accountType;
    }
}