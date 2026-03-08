package io.github.Lucasfcz.fluxbank.repository;

import io.github.Lucasfcz.fluxbank.domain.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @EntityGraph(attributePaths = {"fromAccount", "toAccount"})
    Page<Transaction> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
            UUID fromAccountId,
            UUID toAccountId,
            Pageable pageable
    );
}
