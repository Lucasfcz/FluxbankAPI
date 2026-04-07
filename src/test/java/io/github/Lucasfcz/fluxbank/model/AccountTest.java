package io.github.Lucasfcz.fluxbank.model;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.AccountInactiveException;
import io.github.Lucasfcz.fluxbank.exception.InsufficientBalanceException;
import io.github.Lucasfcz.fluxbank.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Account Domain Tests")
class AccountTest {

    private Account account;
    private JwtUser owner;

    @BeforeEach
    void setUp() {
        owner = new JwtUser("lucas@email.com", "encodedPassword123");
        owner.setId(1L);
        account = new Account(owner, "Lucas Cabral", "12345678900", "lucas@email.com", AccountType.CHECKING);
    }

     @Nested
     @DisplayName("deposit()")
     class Deposit {

        @Test
        @DisplayName("Should increase balance when amount is valid")
        void shouldIncreaseBalance_WhenAmountIsValid() {
            account.deposit(BigDecimal.valueOf(100));
            assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
        }

        @Test
        @DisplayName("Should accumulate balance on multiple deposits")
        void shouldAccumulateBalance_OnMultipleDeposits() {
            account.deposit(BigDecimal.valueOf(100));
            account.deposit(BigDecimal.valueOf(50));
            assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));
        }

        @Test
        @DisplayName("Should throw InvalidAmountException when amount is zero")
        void shouldThrow_WhenAmountIsZero() {
            assertThatThrownBy(() -> account.deposit(BigDecimal.ZERO))
                    .isInstanceOf(InvalidAmountException.class)
                    .hasMessage("Amount must be greater than zero");
        }

        @Test
        @DisplayName("Should throw InvalidAmountException when amount is negative")
        void shouldThrow_WhenAmountIsNegative() {
            assertThatThrownBy(() -> account.deposit(BigDecimal.valueOf(-50)))
                    .isInstanceOf(InvalidAmountException.class)
                    .hasMessage("Amount must be greater than zero");
        }

        @Test
        @DisplayName("Should throw InvalidAmountException when amount is null")
        void shouldThrow_WhenAmountIsNull() {
            assertThatThrownBy(() -> account.deposit(null))
                    .isInstanceOf(InvalidAmountException.class)
                    .hasMessage("Amount must be greater than zero");
        }

        @Test
        @DisplayName("Should throw AccountInactiveException when account is inactive")
        void shouldThrow_WhenAccountIsInactive() {
            account.deactivate();
            assertThatThrownBy(() -> account.deposit(BigDecimal.valueOf(100)))
                    .isInstanceOf(AccountInactiveException.class)
                    .hasMessage("Account is inactive");
        }
     }

     @Nested
     @DisplayName("withdraw()")
     class Withdraw {

        @Test
        @DisplayName("Should decrease balance when balance is sufficient")
        void shouldDecreaseBalance_WhenBalanceIsSufficient() {
            account.deposit(BigDecimal.valueOf(100));
            account.withdraw(BigDecimal.valueOf(40));
            assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(60));
        }

        @Test
        @DisplayName("Should allow withdrawal of entire balance")
        void shouldAllow_WithdrawEntireBalance() {
            account.deposit(BigDecimal.valueOf(100));
            account.withdraw(BigDecimal.valueOf(100));
            assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should throw InsufficientBalanceException when withdrawal exceeds balance")
        void shouldThrow_WhenBalanceIsInsufficient() {
            account.deposit(BigDecimal.valueOf(50));
            assertThatThrownBy(() -> account.withdraw(BigDecimal.valueOf(100)))
                    .isInstanceOf(InsufficientBalanceException.class)
                    .hasMessage("The amount must be less than balance");
        }

        @Test
        @DisplayName("Should throw InvalidAmountException when amount is zero")
        void shouldThrow_WhenAmountIsZero() {
            account.deposit(BigDecimal.valueOf(100));
            assertThatThrownBy(() -> account.withdraw(BigDecimal.ZERO))
                    .isInstanceOf(InvalidAmountException.class)
                    .hasMessage("Amount must be greater than zero");
        }

        @Test
        @DisplayName("Should throw InvalidAmountException when amount is negative")
        void shouldThrow_WhenAmountIsNegative() {
            account.deposit(BigDecimal.valueOf(100));
            assertThatThrownBy(() -> account.withdraw(BigDecimal.valueOf(-10)))
                    .isInstanceOf(InvalidAmountException.class)
                    .hasMessage("Amount must be greater than zero");
        }

        @Test
        @DisplayName("Should throw AccountInactiveException when account is inactive")
        void shouldThrow_WhenAccountIsInactive() {
            account.deposit(BigDecimal.valueOf(100));
            account.deactivate();
            assertThatThrownBy(() -> account.withdraw(BigDecimal.valueOf(50)))
                    .isInstanceOf(AccountInactiveException.class)
                    .hasMessage("Account is inactive");
        }
     }

     @Nested
     @DisplayName("deactivate()")
     class Deactivate {

        @Test
        @DisplayName("Should set account as inactive")
        void shouldSetAccountAsInactive() {
            assertThat(account.isActive()).isTrue();
            account.deactivate();
            assertThat(account.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should preserve balance after deactivation")
        void shouldPreserveBalance_AfterDeactivation() {
            account.deposit(BigDecimal.valueOf(200));
            account.deactivate();
            assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(200));
        }
     }

     @Nested
     @DisplayName("change methods")
     class ChangeMethods {

        @Test
        @DisplayName("Should update email")
        void shouldUpdateEmail() {
            account.changeEmail("novo@email.com");
            assertThat(account.getEmail()).isEqualTo("novo@email.com");
        }

        @Test
        @DisplayName("Should update holder name")
        void shouldUpdateHolderName() {
            account.changeHolderName("Maria Souza");
            assertThat(account.getHolderName()).isEqualTo("Maria Souza");
        }

        @Test
        @DisplayName("Should update account type")
        void shouldUpdateAccountType() {
            account.changeAccountType(AccountType.SAVINGS);
            assertThat(account.getAccountType()).isEqualTo(AccountType.SAVINGS);
        }
    }
}