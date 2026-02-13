package javadev.project.consumer.repository;

import javadev.project.consumer.entity.transactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Transaction History entity
 * Provides CRUD operations and custom queries for transaction history
 * management
 */
@Repository
public interface TransactionHistoryRepository extends JpaRepository<transactionHistory, Integer> {

    /**
     * Find all transactions by date
     * 
     * @param transactionDate Transaction date
     * @return List of transaction history
     */
    List<transactionHistory> findByTransactionDate(LocalDate transactionDate);

    /**
     * Find transactions by date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return List of transaction history
     */
    List<transactionHistory> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
}
