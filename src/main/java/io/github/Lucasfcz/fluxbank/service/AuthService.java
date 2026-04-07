package io.github.Lucasfcz.fluxbank.service;

import io.github.Lucasfcz.fluxbank.exception.IdNotFoundException;
import io.github.Lucasfcz.fluxbank.exception.ResourceConflictException;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import io.github.Lucasfcz.fluxbank.repository.JwtUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final JwtUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws IdNotFoundException {
        return repository.findUserByEmail(email).orElseThrow(() -> new IdNotFoundException("User not found with email: " + email));
    }

    public JwtUser getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceConflictException("User not authenticated");
        }

        String email = authentication.getName();

        return repository.findUserByEmail(email)
                .orElseThrow(() -> new IdNotFoundException("User not found"));
    }
}
