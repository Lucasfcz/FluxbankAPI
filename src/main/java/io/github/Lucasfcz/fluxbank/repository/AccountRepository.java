package io.github.Lucasfcz.fluxbank.repository;

import io.github.Lucasfcz.fluxbank.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    // Lookup used to enforce or validate unique cpf constraints in business flows.
    Optional<Account> findByCpf(String cpf);

    // Lookup used to enforce or validate unique email constraints in business flows.
    Optional<Account> findByEmail(String email);
}
