package project.booteco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.booteco.domain.StatusTransaction;
import project.booteco.domain.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByUserIdAndShortCode(UUID userId,String shortCode);
    List<Transaction> findByUserIdAndStatus(UUID userId, StatusTransaction status);
}
