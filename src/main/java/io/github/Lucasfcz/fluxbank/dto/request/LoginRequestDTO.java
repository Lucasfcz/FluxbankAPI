package io.github.Lucasfcz.fluxbank.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * User login request DTO.
 * Both email and password are required for authentication.
 */
@Schema(description = "User login request")
public record LoginRequestDTO(
        @Schema(description = "Email associated with the user account", example = "user@example.com")
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @Schema(description = "Account password", example = "SecurePassword123")
        @NotBlank(message = "Password is required")
        String password
) {}


