package io.github.Lucasfcz.fluxbank.model.model;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.InsufficientBalanceException;
import io.github.Lucasfcz.fluxbank.exception.InvalidAmountException;
import io.github.Lucasfcz.fluxbank.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        // Given: A newly created account
        account = new Account("Lucas", "12345678900", "lucas@email.com", AccountType.CHECKING);
    }

    @Test
    @DisplayName("Should deposit successfully and increase balance when amount is valid")
    void deposit_ShouldIncreaseBalance_WhenAmountIsValid() {
        // When: Depositing 100
        account.deposit(BigDecimal.valueOf(100));

        // Then: Balance must be 100
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Should throw InvalidAmountException when depositing zero or negative values")
    void deposit_ShouldThrowException_WhenAmountIsInvalid() {
        // When / Then
        assertThatThrownBy(() -> account.deposit(BigDecimal.ZERO))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must be greater than zero");
    }

    @Test
    @DisplayName("Should withdraw successfully when balance is sufficient")
    void withdraw_ShouldDecreaseBalance_WhenBalanceIsSufficient() {
        // Given
        account.deposit(BigDecimal.valueOf(100));

        // When
        account.withdraw(BigDecimal.valueOf(40));

        // Then
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(60));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when withdrawal exceeds balance")
    void withdraw_ShouldThrowException_WhenBalanceIsInsufficient() {
        // Given
        account.deposit(BigDecimal.valueOf(50));

        // When / Then
        assertThatThrownBy(() -> account.withdraw(BigDecimal.valueOf(100)))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("The amount must be less than balance");
    }
}

