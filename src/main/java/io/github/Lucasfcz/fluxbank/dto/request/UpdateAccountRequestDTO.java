package io.github.Lucasfcz.fluxbank.dto.request;

import io.github.Lucasfcz.fluxbank.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * Bank account update request DTO.
 * All fields are optional - only provided fields will be updated.
 * Email must remain unique if changed.
 */
@Schema(description = "Bank account update request - all fields optional")
public record UpdateAccountRequestDTO(
        @Nullable
        @Schema(description = "New holder name (optional, if null will not be updated)", example = "Jane Doe")
        String holderName,

        @Nullable
        @Schema(description = "New email address - must be unique if provided (optional)", example = "jane@example.com")
        String email,

        @Nullable
        @Schema(description = "New account type (optional, if null will not be updated)", example = "SAVINGS")
        AccountType accountType
) {}
