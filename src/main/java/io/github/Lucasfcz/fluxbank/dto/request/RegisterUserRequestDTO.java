package io.github.Lucasfcz.fluxbank.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequestDTO(
        @NotEmpty
        String username,

        @NotEmpty
        String email,

        @NotEmpty
        String password
) {
}
