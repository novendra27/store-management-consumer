package javadev.project.consumer.repository;

import javadev.project.consumer.entity.transactionDetail;
import javadev.project.consumer.entity.transactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionDetailRepository extends JpaRepository<transactionDetail, Integer> {

    /**
     * Find all transaction details by transaction history
     * 
     * @param transactionHistory Transaction history
     * @return List of transaction details
     */
    List<transactionDetail> findByTransactionHistory(transactionHistory transactionHistory);

    /**
     * Calculate total price for a transaction
     * 
     * @param transactionId Transaction ID
     * @return Total price
     */
    @Query("SELECT SUM(td.totalPrice) FROM transactionDetail td WHERE td.transactionHistory.id = :transactionId")
    BigDecimal calculateTotalPriceByTransactionId(Integer transactionId);
}
