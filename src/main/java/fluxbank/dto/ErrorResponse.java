package fluxbank.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp, String message
) {}