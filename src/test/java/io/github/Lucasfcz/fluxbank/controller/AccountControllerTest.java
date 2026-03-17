package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.GlobalExceptionHandler;
import io.github.Lucasfcz.fluxbank.exception.IdNotFoundException;
import io.github.Lucasfcz.fluxbank.exception.ResourceConflictException;
import io.github.Lucasfcz.fluxbank.exception.SameAccountException;
import io.github.Lucasfcz.fluxbank.service.AccountService;
import io.github.Lucasfcz.fluxbank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService service;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AccountController controller;

    @BeforeEach
    void setup() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    // ========================= CREATE =========================

    @Test
    void shouldCreateAccountAndReturn201() throws Exception {
        Account account = new Account(
                "Lucas Cabral",
                "12345678900",
                "lucas@email.com",
                AccountType.CHECKING
        );
        ReflectionTestUtils.setField(account, "id", UUID.randomUUID());

        when(service.createAccount(
                any(), any(), any(), any()
        )).thenReturn(account);

        String requestBody = """
                {
                  "holderName": "Lucas Cabral",
                  "cpf": "12345678900",
                  "email": "lucas@email.com",
                  "accountType": "CHECKING"
                }
                """;

        mockMvc.perform(post("/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(account.getId().toString()))
                .andExpect(jsonPath("$.balance").value(0));

        verify(service).createAccount(
                "Lucas Cabral",
                "12345678900",
                "lucas@email.com",
                AccountType.CHECKING
        );
    }

    @Test
    void shouldReturn400WhenCreateAccountPayloadIsInvalid() throws Exception {
        String invalidBody = """
                {
                  "holderName": "",
                  "cpf": "",
                  "email": "email-invalido",
                  "accountType": null
                }
                """;

        mockMvc.perform(post("/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenCreateAccountCpfAlreadyExists() throws Exception {
        when(service.createAccount(any(), any(), any(), any()))
                .thenThrow(new ResourceConflictException("CPF is already registered"));

        String requestBody = """
                {
                  "holderName": "Lucas Cabral",
                  "cpf": "12345678900",
                  "email": "lucas@email.com",
                  "accountType": "CHECKING"
                }
                """;

        mockMvc.perform(post("/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("CPF is already registered"));
    }

    // ========================= DEPOSIT =========================

    @Test
    void shouldDepositAndReturn200() throws Exception {
        Account account = new Account(
                "Lucas Cabral",
                "12345678900",
                "lucas@email.com",
                AccountType.CHECKING
        );
        ReflectionTestUtils.setField(account, "id", UUID.randomUUID());

        UUID accountId = account.getId();
        account.deposit(BigDecimal.valueOf(100));

        when(transactionService.deposit(accountId, BigDecimal.valueOf(100)))
                .thenReturn(account);

        String body = """
                {
                  "accountId": "%s",
                  "amount": 100
                }
                """.formatted(accountId);

        mockMvc.perform(post("/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.balance").value(100));

        verify(transactionService).deposit(accountId, BigDecimal.valueOf(100));
    }

    @Test
    void shouldReturn404WhenDepositAccountDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        when(transactionService.deposit(id, BigDecimal.TEN))
                .thenThrow(new IdNotFoundException("Account Id not found"));

        String body = """
                {
                  "accountId": "%s",
                  "amount": 10
                }
                """.formatted(id);

        mockMvc.perform(post("/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account Id not found"));
    }

    @Test
    void shouldReturn400WhenDepositPayloadIsInvalid() throws Exception {
        String body = """
                {
                  "accountId": null,
                  "amount": 0
                }
                """;

        mockMvc.perform(post("/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ========================= TRANSFER =========================

    @Test
    void shouldTransferAndReturn200() throws Exception {
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();

        String body = """
                {
                  "fromId": "%s",
                  "toId": "%s",
                  "amount": 25
                }
                """.formatted(from, to);

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful"));

        verify(transactionService).transfer(from, to, BigDecimal.valueOf(25));
    }

    @Test
    void shouldReturn400WhenTransferToSameAccount() throws Exception {
        UUID sameId = UUID.randomUUID();

        doThrow(new SameAccountException("Cannot transfer to the same account"))
                .when(transactionService).transfer(sameId, sameId, BigDecimal.valueOf(30));

        String body = """
                {
                  "fromId": "%s",
                  "toId": "%s",
                  "amount": 30
                }
                """.formatted(sameId, sameId);

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Cannot transfer to the same account"));
    }
}
