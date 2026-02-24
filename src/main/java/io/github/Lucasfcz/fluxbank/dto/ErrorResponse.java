package io.github.Lucasfcz.fluxbank.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp, // Server timestamp when the error was produced.
        String message // Human-readable error description.
) {}
