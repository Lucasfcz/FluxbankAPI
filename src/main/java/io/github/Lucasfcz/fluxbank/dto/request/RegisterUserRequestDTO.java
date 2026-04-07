package io.github.Lucasfcz.fluxbank.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User registration request DTO.
 * Email is used as the unique identifier and username for authentication.
 * Password will be encrypted with BCrypt before storage.
 */
@Schema(description = "User registration request")
public record RegisterUserRequestDTO(
        @Schema(description = "User email (used as username)", example = "user@example.com")
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @Schema(description = "Account password (will be encrypted)", example = "SecurePassword123")
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {}

