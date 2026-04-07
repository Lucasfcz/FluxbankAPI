package io.github.Lucasfcz.fluxbank.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * User registration response DTO.
 * Returns the newly created user information.
 */
@Schema(description = "User registration confirmation response")
public record RegisterUserResponseDTO(
        @Schema(description = "User email (used as username for authentication)", example = "user@example.com")
        String email
) {}

