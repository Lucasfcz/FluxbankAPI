package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.dto.request.LoginRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.request.RegisterUserRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.response.LoginResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.RegisterUserResponseDTO;
import io.github.Lucasfcz.fluxbank.mapper.AuthMapper;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import io.github.Lucasfcz.fluxbank.repository.JwtUserRepository;
import io.github.Lucasfcz.fluxbank.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication Controller.
 *
 * Provides public endpoints (without JWT requirement) for:
 * - POST /auth/register: Register new user
 * - POST /auth/login: Perform login and obtain JWT token
 *
 * Obtained JWT tokens are valid for 24 hours and must be included
 * in all subsequent requests in the header: Authorization: Bearer <token>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Authentication",
    description = "Public endpoints for user registration and authentication. Issues JWT token valid for 24 hours. " +
                  "Token must be included in subsequent requests: Authorization: Bearer <token>. " +
                  "These endpoints DO NOT require authentication."
)
public class AuthController {

    private final JwtUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    @Operation(
        summary = "User login",
        description = "Authenticates a user with email and password. If successful, returns a JWT token valid for 24 hours. " +
                      "The token must be included in all subsequent requests in the Authorization: Bearer <token> header"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login successful - JWT token returned",
            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid credentials (wrong email or password) or malformed request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication failed"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        UsernamePasswordAuthenticationToken userAndPassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPassword);

        JwtUser user = (JwtUser) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(
        summary = "Register new user",
        description = "Creates a new user account for JWT authentication. Password is encrypted with BCrypt. " +
                      "Email must be unique in the system. After registration, use /auth/login to obtain a token."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = RegisterUserResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data (malformed email, incomplete request, etc)"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already registered"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO request) {
        JwtUser newUser = new JwtUser(
                request.email(),
                passwordEncoder.encode(request.password())
        );

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(authMapper.toRegisterUserResponseDTO(newUser));
    }
}