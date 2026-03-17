package io.github.Lucasfcz.fluxbank.service;


import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.IdNotFoundException;
import io.github.Lucasfcz.fluxbank.exception.ResourceConflictException;
import io.github.Lucasfcz.fluxbank.exception.SameAccountException;
import io.github.Lucasfcz.fluxbank.repository.AccountRepository;
import io.github.Lucasfcz.fluxbank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService service;

    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setup() {
        sourceAccount = new Account(
                "Lucas Cabral",
                "12345678900",
                "lucas@email.com",
                AccountType.CHECKING
        );

        destinationAccount = new Account(
                "Maria Souza",
                "99988877766",
                "maria@email.com",
                AccountType.SAVINGS
        );

        ReflectionTestUtils.setField(sourceAccount, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(destinationAccount, "id", UUID.randomUUID());

        sourceAccount.deposit(BigDecimal.valueOf(200));
    }

    @Test
    void shouldCreateAccountSuccessfully() {
        when(repository.findByCpf("12345678900")).thenReturn(Optional.empty());
        when(repository.findByEmail("lucas@email.com")).thenReturn(Optional.empty());
        when(repository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = service.createAccount(
                "Lucas Cabral",
                "12345678900",
                "lucas@email.com",
                AccountType.CHECKING
        );

        assertNotNull(result);
        assertEquals("Lucas Cabral", result.getHolderName());
        assertEquals("12345678900", result.getCpf());
        assertEquals("lucas@email.com", result.getEmail());
        assertEquals(AccountType.CHECKING, result.getAccountType());
        assertEquals(BigDecimal.ZERO, result.getBalance());

        verify(repository).save(any(Account.class));
    }

    @Test
    void shouldThrowWhenCpfAlreadyExists() {
        when(repository.findByCpf("12345678900")).thenReturn(Optional.of(sourceAccount));

        assertThrows(ResourceConflictException.class, () -> service.createAccount(
                "Lucas Cabral",
                "12345678900",
                "new@email.com",
                AccountType.CHECKING
        ));
        verify(repository).findByCpf("12345678900");
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(repository.findByCpf("12345678900")).thenReturn(Optional.empty());
        when(repository.findByEmail("lucas@email.com")).thenReturn(Optional.of(sourceAccount));

        assertThrows(ResourceConflictException.class, () -> service.createAccount(
                "Lucas Cabral",
                "12345678900",
                "lucas@email.com",
                AccountType.CHECKING
        ));
        verify(repository).findByEmail("lucas@email.com");
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    void shouldDepositSuccessfully() {
        UUID accountId = sourceAccount.getId();
        BigDecimal amount = BigDecimal.valueOf(50);

        when(repository.findById(accountId)).thenReturn(Optional.of(sourceAccount));

        Account result = service.deposit(accountId, amount);

        assertEquals(sourceAccount, result);
        assertEquals(BigDecimal.valueOf(250), result.getBalance());
        verify(repository).findById(accountId);
    }

    @Test
    void shouldThrowWhenDepositAccountNotFound() {
        UUID accountId = UUID.randomUUID();

        when(repository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> service.deposit(accountId, BigDecimal.TEN));
        verify(repository).findById(accountId);
    }

    @Test
    void shouldWithdrawSuccessfully() {
        UUID accountId = sourceAccount.getId();
        BigDecimal amount = BigDecimal.valueOf(75);

        when(repository.findById(accountId)).thenReturn(Optional.of(sourceAccount));

        Account result = service.withdraw(accountId, amount);

        assertEquals(sourceAccount, result);
        assertEquals(BigDecimal.valueOf(125), result.getBalance());
        verify(repository).findById(accountId);
    }

    @Test
    void shouldThrowWhenWithdrawAccountNotFound() {
        UUID accountId = UUID.randomUUID();

        when(repository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> service.withdraw(accountId, BigDecimal.TEN));
        verify(repository).findById(accountId);
    }

    @Test
    void shouldTransferSuccessfully() {
        UUID fromId = sourceAccount.getId();
        UUID toId = destinationAccount.getId();
        BigDecimal amount = BigDecimal.valueOf(80);

        when(repository.findById(fromId)).thenReturn(Optional.of(sourceAccount));
        when(repository.findById(toId)).thenReturn(Optional.of(destinationAccount));

        service.transfer(fromId, toId, amount);

        assertEquals(BigDecimal.valueOf(120), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(80), destinationAccount.getBalance());
        verify(repository).findById(fromId);
        verify(repository).findById(toId);
    }

    @Test
    void shouldThrowWhenTransferToSameAccount() {
        UUID sameId = sourceAccount.getId();

        assertThrows(SameAccountException.class, () -> service.transfer(sameId, sameId, BigDecimal.TEN));
        verify(repository, never()).findById(any(UUID.class));
    }

    @Test
    void shouldThrowWhenTransferSourceAccountNotFound() {
        UUID fromId = UUID.randomUUID();
        UUID toId = destinationAccount.getId();

        when(repository.findById(fromId)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> service.transfer(fromId, toId, BigDecimal.TEN));
        verify(repository).findById(fromId);
        verify(repository, never()).findById(toId);
    }

    @Test
    void shouldThrowWhenTransferDestinationAccountNotFound() {
        UUID fromId = sourceAccount.getId();
        UUID toId = UUID.randomUUID();

        when(repository.findById(fromId)).thenReturn(Optional.of(sourceAccount));
        when(repository.findById(toId)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> service.transfer(fromId, toId, BigDecimal.TEN));
        verify(repository).findById(fromId);
        verify(repository).findById(toId);
    }
}
