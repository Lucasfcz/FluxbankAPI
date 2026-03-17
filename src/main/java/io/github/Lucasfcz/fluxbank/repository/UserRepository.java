package io.github.Lucasfcz.fluxbank.repository;

import io.github.Lucasfcz.fluxbank.model.JwtUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<JwtUser, Long> {

    Optional<UserDetails> findUserByEmail(String email);
}
