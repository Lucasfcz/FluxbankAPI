package io.github.Lucasfcz.fluxbank.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp, String message
) {}