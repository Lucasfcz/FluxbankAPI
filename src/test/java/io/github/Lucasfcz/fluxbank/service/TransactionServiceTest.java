package io.github.Lucasfcz.fluxbank.service;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.AccountInactiveException;
import io.github.Lucasfcz.fluxbank.exception.IdNotFoundException;
import io.github.Lucasfcz.fluxbank.exception.InsufficientBalanceException;
import io.github.Lucasfcz.fluxbank.exception.SameAccountException;
import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import io.github.Lucasfcz.fluxbank.model.Transaction;
import io.github.Lucasfcz.fluxbank.repository.AccountRepository;
import io.github.Lucasfcz.fluxbank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService service;

    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        JwtUser owner1 = new JwtUser("lucas@email.com", "encodedPassword123");
        owner1.setId(1L);
        JwtUser owner2 = new JwtUser("maria@email.com", "encodedPassword456");
        owner2.setId(2L);

        sourceAccount = new Account(owner1, "Lucas Cabral", "12345678900", "lucas@email.com", AccountType.CHECKING);
        destinationAccount = new Account(owner2, "Maria Souza", "99988877766", "maria@email.com", AccountType.SAVINGS);

        ReflectionTestUtils.setField(sourceAccount, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(destinationAccount, "id", UUID.randomUUID());

        sourceAccount.deposit(BigDecimal.valueOf(500));
    }

    // ========================= DEPOSIT =========================

    @Nested
    @DisplayName("deposit()")
    class Deposit {

        @Test
        @DisplayName("Should deposit and return updated account")
        void shouldDeposit_AndReturnUpdatedAccount() {
            when(repository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

            Account result = service.deposit(sourceAccount.getId(), BigDecimal.valueOf(100));

            assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(600));
            verify(transactionRepository).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Should throw IdNotFoundException when account does not exist")
        void shouldThrow_WhenAccountNotFound() {
            UUID id = UUID.randomUUID();
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deposit(id, BigDecimal.valueOf(100)))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Account Id not found");

            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw AccountInactiveException when account is inactive")
        void shouldThrow_WhenAccountIsInactive() {
            sourceAccount.deactivate();
            when(repository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));

            assertThatThrownBy(() -> service.deposit(sourceAccount.getId(), BigDecimal.valueOf(100)))
                    .isInstanceOf(AccountInactiveException.class)
                    .hasMessage("Account is inactive");

            verify(transactionRepository, never()).save(any());
        }
    }

    // ========================= WITHDRAW =========================

    @Nested
    @DisplayName("withdraw()")
    class Withdraw {

        @Test
        @DisplayName("Should withdraw and return updated account")
        void shouldWithdraw_AndReturnUpdatedAccount() {
            when(repository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

            Account result = service.withdraw(sourceAccount.getId(), BigDecimal.valueOf(200));

            assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(300));
            verify(transactionRepository).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Should throw IdNotFoundException when account does not exist")
        void shouldThrow_WhenAccountNotFound() {
            UUID id = UUID.randomUUID();
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.withdraw(id, BigDecimal.valueOf(100)))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Account Id not found");

            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InsufficientBalanceException when balance is not enough")
        void shouldThrow_WhenBalanceIsInsufficient() {
            when(repository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));

            assertThatThrownBy(() -> service.withdraw(sourceAccount.getId(), BigDecimal.valueOf(9999)))
                    .isInstanceOf(InsufficientBalanceException.class);

            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw AccountInactiveException when account is inactive")
        void shouldThrow_WhenAccountIsInactive() {
            sourceAccount.deactivate();
            when(repository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));

            assertThatThrownBy(() -> service.withdraw(sourceAccount.getId(), BigDecimal.valueOf(100)))
                    .isInstanceOf(AccountInactiveException.class)
                    .hasMessage("Account is inactive");

            verify(transactionRepository, never()).save(any());
        }
    }

    // ========================= TRANSFER =========================

    @Nested
    @DisplayName("transfer()")
    class Transfer {

        @Test
        @DisplayName("Should transfer amount between two accounts atomically")
        void shouldTransfer_BetweenTwoAccounts() {
            when(repository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
            when(repository.findById(destinationAccount.getId())).thenReturn(Optional.of(destinationAccount));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

            service.transfer(sourceAccount.getId(), destinationAccount.getId(), BigDecimal.valueOf(200));

            assertThat(sourceAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(300));
            assertThat(destinationAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(200));
            verify(transactionRepository).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Should throw SameAccountException when fromId equals toId")
        void shouldThrow_WhenFromIdEqualsToId() {
            UUID sameId = sourceAccount.getId();

            assertThatThrownBy(() -> service.transfer(sameId, sameId, BigDecimal.valueOf(100)))
                    .isInstanceOf(SameAccountException.class)
                    .hasMessage("Cannot transfer to the same account");

            verify(repository, never()).findById(any());
            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw IdNotFoundException when source account does not exist")
        void shouldThrow_WhenSourceAccountNotFound() {
            UUID fromId = UUID.randomUUID();
            when(repository.findById(fromId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.transfer(fromId, destinationAccount.getId(), BigDecimal.valueOf(100)))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Source account not found");

            verify(repository, never()).findById(destinationAccount.getId());
            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw IdNotFoundException when destination account does not exist")
        void shouldThrow_WhenDestinationAccountNotFound() {
            UUID toId = UUID.randomUUID();
            when(repository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
            when(repository.findById(toId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.transfer(sourceAccount.getId(), toId, BigDecimal.valueOf(100)))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Destination account not found");

            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InsufficientBalanceException when source has insufficient balance")
        void shouldThrow_WhenSourceBalanceIsInsufficient() {
            when(repository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
            when(repository.findById(destinationAccount.getId())).thenReturn(Optional.of(destinationAccount));

            assertThatThrownBy(() -> service.transfer(sourceAccount.getId(), destinationAccount.getId(), BigDecimal.valueOf(9999)))
                    .isInstanceOf(InsufficientBalanceException.class);

            // Destination balance must remain unchanged
            assertThat(destinationAccount.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw AccountInactiveException when source account is inactive")
        void shouldThrow_WhenSourceAccountIsInactive() {
            sourceAccount.deactivate();
            when(repository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
            when(repository.findById(destinationAccount.getId())).thenReturn(Optional.of(destinationAccount));

            assertThatThrownBy(() -> service.transfer(sourceAccount.getId(), destinationAccount.getId(), BigDecimal.valueOf(100)))
                    .isInstanceOf(AccountInactiveException.class);

            verify(transactionRepository, never()).save(any());
        }
    }
}
