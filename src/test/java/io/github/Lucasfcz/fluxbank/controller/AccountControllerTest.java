package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.dto.response.AccountResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.FindCpfResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.FindEmailResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.TransactionResponseDTO;
import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.*;
import io.github.Lucasfcz.fluxbank.mapper.AccountMapper;
import io.github.Lucasfcz.fluxbank.mapper.TransactionMapper;
import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import io.github.Lucasfcz.fluxbank.service.AccountService;
import io.github.Lucasfcz.fluxbank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountController Tests")
class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService service;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private AccountController controller;

    private Account account;
    private UUID accountId;

    @BeforeEach
    void setup() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        JwtUser owner = new JwtUser("lucas@email.com", "encodedPassword123");
        owner.setId(1L);
        account = new Account(owner, "Lucas Cabral", "12345678900", "lucas@email.com", AccountType.CHECKING);
        accountId = UUID.randomUUID();
        ReflectionTestUtils.setField(account, "id", accountId);

        // Setup mapper mocks to return DTOs dynamically based on account parameter
        when(accountMapper.toAccountResponseDTO(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            return new AccountResponseDTO(
                    acc.getId(), acc.getHolderName(), acc.getEmail(), acc.getAccountType(),
                    acc.getBalance(), acc.isActive(), acc.getCreatedAt()
            );
        });
        when(accountMapper.toFindEmailResponseDTO(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            return new FindEmailResponseDTO(acc.getEmail(), acc.getBalance());
        });
        when(accountMapper.toFindCpfResponseDTO(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            return new FindCpfResponseDTO(acc.getCpf(), acc.getBalance());
        });
    }

    // ========================= CREATE =========================

    @Nested
    @DisplayName("POST /accounts")
    class CreateAccount {

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 201 with account data when creation succeeds")
        void shouldReturn201_WhenCreationSucceeds() throws Exception {
            when(service.createAccount(any(), any(), any(), any())).thenReturn(account);

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "holderName": "Lucas Cabral",
                                      "cpf": "12345678900",
                                      "email": "lucas@email.com",
                                      "accountType": "CHECKING"
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                    .andExpect(jsonPath("$.balance").value(0));

            verify(service).createAccount("Lucas Cabral", "12345678900", "lucas@email.com", AccountType.CHECKING);
        }

        @Test
        @DisplayName("Should return 400 when payload is invalid")
        void shouldReturn400_WhenPayloadIsInvalid() throws Exception {
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "holderName": "",
                                      "cpf": "",
                                      "email": "email-invalido",
                                      "accountType": null
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 409 when CPF already exists")
        void shouldReturn409_WhenCpfAlreadyExists() throws Exception {
            when(service.createAccount(any(), any(), any(), any()))
                    .thenThrow(new ResourceConflictException("CPF is already registered"));

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "holderName": "Lucas Cabral",
                                      "cpf": "12345678900",
                                      "email": "lucas@email.com",
                                      "accountType": "CHECKING"
                                    }
                                    """))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("CPF is already registered"));
        }

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409_WhenEmailAlreadyExists() throws Exception {
            when(service.createAccount(any(), any(), any(), any()))
                    .thenThrow(new ResourceConflictException("Email is already registered"));

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "holderName": "Lucas Cabral",
                                      "cpf": "12345678900",
                                      "email": "lucas@email.com",
                                      "accountType": "CHECKING"
                                    }
                                    """))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Email is already registered"));
        }
    }

    // ========================= FIND BY ID =========================

    @Nested
    @DisplayName("GET /accounts/{accountId}")
    class FindById {

        @Test
        @DisplayName("Should return 200 with account when found")
        void shouldReturn200_WhenAccountFound() throws Exception {
            when(service.findById(accountId)).thenReturn(account);

            mockMvc.perform(get("/accounts/{id}", accountId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                    .andExpect(jsonPath("$.balance").value(0));
        }

        @Test
        @DisplayName("Should return 404 when account not found")
        void shouldReturn404_WhenAccountNotFound() throws Exception {
            when(service.findById(accountId)).thenThrow(new IdNotFoundException("Account Id not found in system"));

            mockMvc.perform(get("/accounts/{id}", accountId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Account Id not found in system"));
        }
    }

    // ========================= FIND ALL =========================

    @Nested
    @DisplayName("GET /accounts")
    class FindAll {

        @Test
        @DisplayName("Should return 200 with list of accounts")
        void shouldReturn200_WithListOfAccounts() throws Exception {
            JwtUser owner2 = new JwtUser("maria@email.com", "encodedPassword456");
            owner2.setId(2L);
            Account other = new Account(owner2, "Maria", "99988877766", "maria@email.com", AccountType.SAVINGS);
            ReflectionTestUtils.setField(other, "id", UUID.randomUUID());

            when(service.findAll()).thenReturn(List.of(account, other));

            mockMvc.perform(get("/accounts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Should return 200 with empty list when no accounts exist")
        void shouldReturn200_WithEmptyList() throws Exception {
            when(service.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/accounts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    // ========================= DEACTIVATE =========================

    @Nested
    @DisplayName("DELETE /accounts/{accountId}")
    class DeactivateAccount {

        @Test
        @DisplayName("Should return 204 when deactivation succeeds")
        void shouldReturn204_WhenDeactivationSucceeds() throws Exception {
            doNothing().when(service).deactivateAccount(accountId);

            mockMvc.perform(delete("/accounts/{id}", accountId))
                    .andExpect(status().isNoContent());

            verify(service).deactivateAccount(accountId);
        }

        @Test
        @DisplayName("Should return 404 when account not found")
        void shouldReturn404_WhenAccountNotFound() throws Exception {
            doThrow(new IdNotFoundException("Account Id not found in system"))
                    .when(service).deactivateAccount(accountId);

            mockMvc.perform(delete("/accounts/{id}", accountId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Account Id not found in system"));
        }
    }

    // ========================= UPDATE =========================

    @Nested
    @DisplayName("PATCH /accounts/{accountId}")
    class UpdateAccount {

        @Test
        @DisplayName("Should return 200 when update succeeds")
        void shouldReturn200_WhenUpdateSucceeds() throws Exception {
            when(service.updateAccount(any(), any(), any(), any())).thenReturn(account);

            mockMvc.perform(patch("/accounts/{id}", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "holderName": "Novo Nome",
                                      "email": null,
                                      "accountType": null
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId.toString()));
        }

        @Test
        @DisplayName("Should return 404 when account not found")
        void shouldReturn404_WhenAccountNotFound() throws Exception {
            when(service.updateAccount(any(), any(), any(), any()))
                    .thenThrow(new IdNotFoundException("Account Id not found"));

            mockMvc.perform(patch("/accounts/{id}", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "holderName": "Novo Nome"
                                    }
                                    """))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Account Id not found"));
        }

        @Test
        @DisplayName("Should return 409 when new email is already in use")
        void shouldReturn409_WhenEmailAlreadyInUse() throws Exception {
            when(service.updateAccount(any(), any(), any(), any()))
                    .thenThrow(new ResourceConflictException("Email is already registered"));

            mockMvc.perform(patch("/accounts/{id}", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "email": "ocupado@email.com"
                                    }
                                    """))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Email is already registered"));
        }
    }
}