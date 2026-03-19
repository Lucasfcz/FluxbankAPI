package io.github.Lucasfcz.fluxbank.service;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.IdNotFoundException;
import io.github.Lucasfcz.fluxbank.exception.ResourceConflictException;
import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("Lucas Cabral", "12345678900", "lucas@email.com", AccountType.CHECKING);
        ReflectionTestUtils.setField(account, "id", UUID.randomUUID());
    }

    // ========================= CREATE =========================

    @Nested
    @DisplayName("createAccount()")
    class CreateAccount {

        @Test
        @DisplayName("Should create account successfully when CPF and email are unique")
        void shouldCreateAccount_WhenCpfAndEmailAreUnique() {
            when(repository.findByCpf("12345678900")).thenReturn(Optional.empty());
            when(repository.findByEmail("lucas@email.com")).thenReturn(Optional.empty());
            when(repository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

            Account result = service.createAccount("Lucas Cabral", "12345678900", "lucas@email.com", AccountType.CHECKING);

            assertThat(result).isNotNull();
            assertThat(result.getHolderName()).isEqualTo("Lucas Cabral");
            assertThat(result.getCpf()).isEqualTo("12345678900");
            assertThat(result.getEmail()).isEqualTo("lucas@email.com");
            assertThat(result.getAccountType()).isEqualTo(AccountType.CHECKING);
            assertThat(result.getBalance()).isEqualByComparingTo("0");
            verify(repository).save(any(Account.class));
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when CPF already exists")
        void shouldThrow_WhenCpfAlreadyExists() {
            when(repository.findByCpf("12345678900")).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> service.createAccount(
                    "Lucas Cabral", "12345678900", "outro@email.com", AccountType.CHECKING))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessage("CPF is already registered");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when email already exists")
        void shouldThrow_WhenEmailAlreadyExists() {
            when(repository.findByCpf("12345678900")).thenReturn(Optional.empty());
            when(repository.findByEmail("lucas@email.com")).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> service.createAccount(
                    "Lucas Cabral", "12345678900", "lucas@email.com", AccountType.CHECKING))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessage("Email is already registered");

            verify(repository, never()).save(any());
        }
    }

    // ========================= FIND =========================

    @Nested
    @DisplayName("find methods")
    class FindMethods {

        @Test
        @DisplayName("Should return account when findById finds a match")
        void shouldReturnAccount_WhenFindByIdFindsMatch() {
            when(repository.findById(account.getId())).thenReturn(Optional.of(account));

            Account result = service.findById(account.getId());

            assertThat(result).isEqualTo(account);
        }

        @Test
        @DisplayName("Should throw IdNotFoundException when findById finds no match")
        void shouldThrow_WhenFindByIdFindsNoMatch() {
            UUID id = UUID.randomUUID();
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(id))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Account Id not found in system");
        }

        @Test
        @DisplayName("Should return account when findByEmail finds a match")
        void shouldReturnAccount_WhenFindByEmailFindsMatch() {
            when(repository.findByEmail("lucas@email.com")).thenReturn(Optional.of(account));

            Account result = service.findByEmail("lucas@email.com");

            assertThat(result).isEqualTo(account);
        }

        @Test
        @DisplayName("Should throw IdNotFoundException when findByEmail finds no match")
        void shouldThrow_WhenFindByEmailFindsNoMatch() {
            when(repository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findByEmail("naoexiste@email.com"))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Email not found in system");
        }

        @Test
        @DisplayName("Should return account when findByCpf finds a match")
        void shouldReturnAccount_WhenFindByCpfFindsMatch() {
            when(repository.findByCpf("12345678900")).thenReturn(Optional.of(account));

            Account result = service.findByCpf("12345678900");

            assertThat(result).isEqualTo(account);
        }

        @Test
        @DisplayName("Should throw IdNotFoundException when findByCpf finds no match")
        void shouldThrow_WhenFindByCpfFindsNoMatch() {
            when(repository.findByCpf("00000000000")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findByCpf("00000000000"))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Cpf not found in system");
        }

        @Test
        @DisplayName("Should return all accounts")
        void shouldReturnAllAccounts() {
            Account other = new Account("Maria", "99988877766", "maria@email.com", AccountType.SAVINGS);
            when(repository.findAll()).thenReturn(List.of(account, other));

            List<Account> result = service.findAll();

            assertThat(result).hasSize(2).contains(account, other);
        }
    }

    // ========================= UPDATE =========================

    @Nested
    @DisplayName("updateAccount()")
    class UpdateAccount {

        @Test
        @DisplayName("Should update holder name successfully")
        void shouldUpdateHolderName() {
            when(repository.findById(account.getId())).thenReturn(Optional.of(account));
            when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

            Account result = service.updateAccount(account.getId(), "Novo Nome", null, null);

            assertThat(result.getHolderName()).isEqualTo("Novo Nome");
        }

        @Test
        @DisplayName("Should update email successfully when new email is unique")
        void shouldUpdateEmail_WhenNewEmailIsUnique() {
            when(repository.findById(account.getId())).thenReturn(Optional.of(account));
            when(repository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
            when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

            Account result = service.updateAccount(account.getId(), null, "novo@email.com", null);

            assertThat(result.getEmail()).isEqualTo("novo@email.com");
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when new email is already in use")
        void shouldThrow_WhenNewEmailAlreadyInUse() {
            Account other = new Account("Maria", "99988877766", "ocupado@email.com", AccountType.SAVINGS);
            when(repository.findById(account.getId())).thenReturn(Optional.of(account));
            when(repository.findByEmail("ocupado@email.com")).thenReturn(Optional.of(other));

            assertThatThrownBy(() -> service.updateAccount(account.getId(), null, "ocupado@email.com", null))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessage("Email is already registered");
        }

        @Test
        @DisplayName("Should not check email conflict when same email is sent")
        void shouldNotCheckConflict_WhenSameEmailIsSent() {
            when(repository.findById(account.getId())).thenReturn(Optional.of(account));
            when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

            Account result = service.updateAccount(account.getId(), null, "lucas@email.com", null);

            verify(repository, never()).findByEmail(any());
            assertThat(result.getEmail()).isEqualTo("lucas@email.com");
        }

        @Test
        @DisplayName("Should update account type successfully")
        void shouldUpdateAccountType() {
            when(repository.findById(account.getId())).thenReturn(Optional.of(account));
            when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

            Account result = service.updateAccount(account.getId(), null, null, AccountType.SAVINGS);

            assertThat(result.getAccountType()).isEqualTo(AccountType.SAVINGS);
        }

        @Test
        @DisplayName("Should throw IdNotFoundException when account does not exist")
        void shouldThrow_WhenAccountNotFound() {
            UUID id = UUID.randomUUID();
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateAccount(id, "Nome", null, null))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Account Id not found");
        }
    }

    // ========================= DEACTIVATE =========================

    @Nested
    @DisplayName("deactivateAccount()")
    class DeactivateAccount {

        @Test
        @DisplayName("Should deactivate account successfully")
        void shouldDeactivateAccount() {
            when(repository.findById(account.getId())).thenReturn(Optional.of(account));

            service.deactivateAccount(account.getId());

            assertThat(account.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should throw IdNotFoundException when account does not exist")
        void shouldThrow_WhenAccountNotFound() {
            UUID id = UUID.randomUUID();
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deactivateAccount(id))
                    .isInstanceOf(IdNotFoundException.class);
        }
    }
}