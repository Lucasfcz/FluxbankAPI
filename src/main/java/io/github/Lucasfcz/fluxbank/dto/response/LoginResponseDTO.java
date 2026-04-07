package io.github.Lucasfcz.fluxbank.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * User login response DTO.
 * Returns the JWT token for authenticated requests.
 */
@Schema(description = "User login response with JWT token")
public record LoginResponseDTO(
        @Schema(description = "JWT Bearer token valid for 24 hours. Use in Authorization header: Bearer <token>",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}

