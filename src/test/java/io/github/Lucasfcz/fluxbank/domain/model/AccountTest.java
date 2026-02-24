package io.github.Lucasfcz.fluxbank.domain.model;

import io.github.Lucasfcz.fluxbank.domain.Account;
import io.github.Lucasfcz.fluxbank.domain.AccountType;
import io.github.Lucasfcz.fluxbank.exception.InsufficientBalanceException;
import io.github.Lucasfcz.fluxbank.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

        @Test
        void shouldIncreaseBalanceWhenDeposit() {
            Account account = new Account(
                    "Lucas",
                    "12345678900",
                    "lucas@email.com",
                    AccountType.CHECKING
            );

            account.deposit(BigDecimal.valueOf(100));

            assertEquals(BigDecimal.valueOf(100), account.getBalance());
        }

    @Test
    void shouldThrowExceptionWhenDepositInvalidAmount() {
        Account account = new Account(
                "Lucas",
                "12345678900",
                "lucas@email.com",
                AccountType.CHECKING
        );

        assertThrows(InvalidAmountException.class, () ->
                account.deposit(BigDecimal.ZERO)
        );
    }
    @Test
    void shouldDecreaseBalanceWhenWithdraw() {
        Account account = new Account(
                "Lucas",
                "12345678900",
                "lucas@email.com",
                AccountType.CHECKING
        );

        account.deposit(BigDecimal.valueOf(200));
        account.withdraw(BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), account.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        Account account = new Account(
                "Lucas",
                "12345678900",
                "lucas@email.com",
                AccountType.CHECKING
        );

        assertThrows(InsufficientBalanceException.class, () ->
                account.withdraw(BigDecimal.valueOf(50))
        );
    }
}

