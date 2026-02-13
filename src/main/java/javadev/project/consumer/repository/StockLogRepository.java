package javadev.project.consumer.repository;

import javadev.project.consumer.entity.stockLog;
import javadev.project.consumer.entity.product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Stock Log entity
 * Provides CRUD operations and custom queries for stock movement tracking
 */
@Repository
public interface StockLogRepository extends JpaRepository<stockLog, Integer> {

    /**
     * Find all stock logs by product
     * 
     * @param product Product entity
     * @return List of stock logs
     */
    List<stockLog> findByProduct(product product);

    /**
     * Find all stock logs by product and log type
     * 
     * @param product Product entity
     * @param logType Log type (e.g., "SALE", "PURCHASE", "ADJUSTMENT")
     * @return List of stock logs
     */
    List<stockLog> findByProductAndLogType(product product, String logType);

    /**
     * Find stock logs by log type
     * 
     * @param logType Log type
     * @return List of stock logs
     */
    List<stockLog> findByLogType(String logType);

    /**
     * Find stock logs by date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return List of stock logs
     */
    List<stockLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
