package io.github.Lucasfcz.fluxbank.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timestamp, // Server timestamp when the error was produced.
        String message // Human-readable error description.
) {}
