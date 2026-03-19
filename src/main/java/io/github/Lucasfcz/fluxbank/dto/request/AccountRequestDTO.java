package io.github.Lucasfcz.fluxbank.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request of an account")
public record AccountRequestDTO(
        @Schema(description = "User id")
        @NotNull(message = "Account ID is required")
        UUID accountId, // Target account for the operation.
        @NotNull
        @Positive
        BigDecimal amount // Positive amount to be processed.
) {}
