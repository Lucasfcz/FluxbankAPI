package io.github.Lucasfcz.fluxbank.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description =  "User login request")
public record LoginRequestDTO(
        @Schema(description = "email of account")
        @NotEmpty
        String email,

        @Schema(description = "account password")
        @NotEmpty
        String password
) {}
