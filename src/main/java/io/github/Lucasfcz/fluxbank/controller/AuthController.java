package io.github.Lucasfcz.fluxbank.controller;

import io.github.Lucasfcz.fluxbank.service.JwtService;
import io.github.Lucasfcz.fluxbank.dto.request.LoginRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.request.RegisterUserRequestDTO;
import io.github.Lucasfcz.fluxbank.dto.response.LoginResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.RegisterUserResponseDTO;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import io.github.Lucasfcz.fluxbank.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Operation(summary = "User login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request){
        UsernamePasswordAuthenticationToken userAndPassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPassword);

        JwtUser user = (JwtUser) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(summary = "Register a new User for jwt")
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO request){
        JwtUser newUser = new JwtUser();
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponseDTO(
                newUser.getUsername(),
                newUser.getEmail()
        ));

    }
}