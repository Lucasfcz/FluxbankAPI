package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.dto.response.AccountResponseDTO;
import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.github.Lucasfcz.fluxbank.exception.*;
import io.github.Lucasfcz.fluxbank.mapper.AccountMapper;
import io.github.Lucasfcz.fluxbank.model.Account;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import io.github.Lucasfcz.fluxbank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TransactionController Tests")
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private TransactionController controller;

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

        // Setup mapper mock to return DTOs dynamically based on account parameter
        when(accountMapper.toAccountResponseDTO(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            return new AccountResponseDTO(
                    acc.getId(), acc.getHolderName(), acc.getEmail(), acc.getAccountType(),
                    acc.getBalance(), acc.isActive(), acc.getCreatedAt()
            );
        });
    }

    // ========================= DEPOSIT =========================

    @Nested
    @DisplayName("POST /transactions/deposit")
    class Deposit {

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 200 with updated balance when deposit succeeds")
        void shouldReturn200_WhenDepositSucceeds() throws Exception {
            account.deposit(BigDecimal.valueOf(100));
            when(transactionService.deposit(accountId, BigDecimal.valueOf(100))).thenReturn(account);

            mockMvc.perform(post("/transactions/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "accountId": "%s",
                                      "amount": 100
                                    }
                                    """.formatted(accountId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                    .andExpect(jsonPath("$.balance").value(100));

            verify(transactionService).deposit(accountId, BigDecimal.valueOf(100));
        }

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 404 when account does not exist")
        void shouldReturn404_WhenAccountNotFound() throws Exception {
            when(transactionService.deposit(accountId, BigDecimal.valueOf(100)))
                    .thenThrow(new IdNotFoundException("Account Id not found"));

            mockMvc.perform(post("/transactions/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "accountId": "%s",
                                      "amount": 100
                                    }
                                    """.formatted(accountId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Account Id not found"));
        }

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 403 when account is inactive")
        void shouldReturn403_WhenAccountIsInactive() throws Exception {
            when(transactionService.deposit(accountId, BigDecimal.valueOf(100)))
                    .thenThrow(new AccountInactiveException("Account is inactive"));

            mockMvc.perform(post("/transactions/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "accountId": "%s",
                                      "amount": 100
                                    }
                                    """.formatted(accountId)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Account is inactive"));
        }

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 400 when payload is invalid")
        void shouldReturn400_WhenPayloadIsInvalid() throws Exception {
            mockMvc.perform(post("/transactions/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "accountId": null,
                                      "amount": 0
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }
    }

    // ========================= WITHDRAW =========================

    @Nested
    @DisplayName("POST /transactions/withdraw")
    class Withdraw {

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 200 with updated balance when withdrawal succeeds")
        void shouldReturn200_WhenWithdrawalSucceeds() throws Exception {
            account.deposit(BigDecimal.valueOf(500));
            account.withdraw(BigDecimal.valueOf(100)); // balance = 400
            when(transactionService.withdraw(accountId, BigDecimal.valueOf(100))).thenReturn(account);

            mockMvc.perform(post("/transactions/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "accountId": "%s",
                                      "amount": 100
                                    }
                                    """.formatted(accountId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                    .andExpect(jsonPath("$.balance").value(400));
        }

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 400 when balance is insufficient")
        void shouldReturn400_WhenBalanceIsInsufficient() throws Exception {
            when(transactionService.withdraw(accountId, BigDecimal.valueOf(9999)))
                    .thenThrow(new InsufficientBalanceException("The amount must be less than balance"));

            mockMvc.perform(post("/transactions/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "accountId": "%s",
                                      "amount": 9999
                                    }
                                    """.formatted(accountId)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("The amount must be less than balance"));
        }

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 403 when account is inactive")
        void shouldReturn403_WhenAccountIsInactive() throws Exception {
            when(transactionService.withdraw(accountId, BigDecimal.valueOf(100)))
                    .thenThrow(new AccountInactiveException("Account is inactive"));

            mockMvc.perform(post("/transactions/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "accountId": "%s",
                                      "amount": 100
                                    }
                                    """.formatted(accountId)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Account is inactive"));
        }

        @Test
        @DisplayName("Should return 404 when account does not exist")
        void shouldReturn404_WhenAccountNotFound() throws Exception {
            when(transactionService.withdraw(accountId, BigDecimal.valueOf(100)))
                    .thenThrow(new IdNotFoundException("Account Id not found"));

            mockMvc.perform(post("/transactions/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "accountId": "%s",
                                      "amount": 100
                                    }
                                    """.formatted(accountId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Account Id not found"));
        }
    }

    // ========================= TRANSFER =========================

    @Nested
    @DisplayName("POST /transactions/transfer")
    class Transfer {

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 200 with success message when transfer succeeds")
        void shouldReturn200_WhenTransferSucceeds() throws Exception {
            UUID fromId = UUID.randomUUID();
            UUID toId = UUID.randomUUID();

            mockMvc.perform(post("/transactions/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "fromId": "%s",
                                      "toId": "%s",
                                      "amount": 100
                                    }
                                    """.formatted(fromId, toId)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Transfer successful"));

            verify(transactionService).transfer(fromId, toId, BigDecimal.valueOf(100));
        }

        @Test
        @WithMockUser(username = "lucas@email.com")
        @DisplayName("Should return 400 when transfer is to same account")
        void shouldReturn400_WhenTransferToSameAccount() throws Exception {
            UUID sameId = UUID.randomUUID();
            doThrow(new SameAccountException("Cannot transfer to the same account"))
                    .when(transactionService).transfer(sameId, sameId, BigDecimal.valueOf(100));

            mockMvc.perform(post("/transactions/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "fromId": "%s",
                                      "toId": "%s",
                                      "amount": 100
                                    }
                                    """.formatted(sameId, sameId)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Cannot transfer to the same account"));
        }

        @Test
        @DisplayName("Should return 404 when source account does not exist")
        void shouldReturn404_WhenSourceAccountNotFound() throws Exception {
            UUID fromId = UUID.randomUUID();
            UUID toId = UUID.randomUUID();
            doThrow(new IdNotFoundException("Source account not found"))
                    .when(transactionService).transfer(fromId, toId, BigDecimal.valueOf(100));

            mockMvc.perform(post("/transactions/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "fromId": "%s",
                                      "toId": "%s",
                                      "amount": 100
                                    }
                                    """.formatted(fromId, toId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Source account not found"));
        }

        @Test
        @DisplayName("Should return 400 when balance is insufficient for transfer")
        void shouldReturn400_WhenBalanceIsInsufficient() throws Exception {
            UUID fromId = UUID.randomUUID();
            UUID toId = UUID.randomUUID();
            doThrow(new InsufficientBalanceException("The amount must be less than balance"))
                    .when(transactionService).transfer(fromId, toId, BigDecimal.valueOf(9999));

            mockMvc.perform(post("/transactions/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "fromId": "%s",
                                      "toId": "%s",
                                      "amount": 9999
                                    }
                                    """.formatted(fromId, toId)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("The amount must be less than balance"));
        }
    }
}