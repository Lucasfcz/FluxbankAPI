package io.github.Lucasfcz.fluxbank.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(
        @NotEmpty
        String email,

        @NotEmpty
        String password
) {}
