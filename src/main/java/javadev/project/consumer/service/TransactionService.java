package javadev.project.consumer.service;

import javadev.project.consumer.dto.TransactionItemDTO;
import javadev.project.consumer.dto.TransactionRequestDTO;
import javadev.project.consumer.entity.product;
import javadev.project.consumer.entity.stockLog;
import javadev.project.consumer.entity.transactionDetail;
import javadev.project.consumer.entity.transactionHistory;
import javadev.project.consumer.exception.BusinessException;
import javadev.project.consumer.exception.ErrorCode;
import javadev.project.consumer.repository.StockLogRepository;
import javadev.project.consumer.repository.TransactionDetailRepository;
import javadev.project.consumer.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Service class for transaction processing business logic
 * Handles complete sales transaction flow including validation, creation, and
 * stock updates
 * All operations are transactional with automatic rollback on errors
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final ProductService productService;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final StockLogRepository stockLogRepository;

    private static final String LOG_TYPE_SALE = "SALE";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Process transaction from Kafka message
     * This method handles the complete transaction flow:
     * 1. Validate products and stock
     * 2. Create transaction history
     * 3. Process each item (insert detail, stock log, update product)
     * 4. Update total price in transaction history
     *
     * @param dto Transaction request DTO from Kafka
     * @return Created transaction history
     * @throws BusinessException if any error occurs during processing
     */
    @Transactional(rollbackFor = Exception.class)
    public transactionHistory processTransaction(TransactionRequestDTO dto) {
        try {
            log.info("Processing transaction: date={}, items count={}",
                    dto.getTransactionDate(), dto.getItems().size());

            // Step 1: Parse and validate transaction date
            LocalDate transactionDate = parseTransactionDate(dto.getTransactionDate());

            // Step 2: Validate all products and stock before processing
            validateTransactionItems(dto);

            // Step 3: Create transaction history
            transactionHistory transaction = createTransactionHistory(transactionDate);
            log.info("Transaction history created with ID: {}", transaction.getId());

            // Step 4: Process each item
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (TransactionItemDTO item : dto.getItems()) {
                BigDecimal itemTotalPrice = processTransactionItem(transaction, item);
                totalPrice = totalPrice.add(itemTotalPrice);
            }

            // Step 5: Update total price in transaction history
            transaction.setTotalPrice(totalPrice);
            transactionHistoryRepository.save(transaction);

            log.info("Transaction processed successfully. ID: {}, Total Price: {}",
                    transaction.getId(), totalPrice);

            return transaction;

        } catch (BusinessException e) {
            // Re-throw BusinessException as-is
            throw e;
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage(), e);
            throw new BusinessException(
                    ErrorCode.TRANSACTION_PROCESSING_ERROR,
                    "Failed to process transaction: " + e.getMessage(),
                    e).addDetail("transactionDate", dto.getTransactionDate())
                    .addDetail("itemCount", dto.getItems().size());
        }
    }

    /**
     * Parse transaction date from string format to LocalDate
     * Expects date in yyyy-MM-dd format
     *
     * @param dateString Date string in yyyy-MM-dd format
     * @return LocalDate object
     * @throws BusinessException if date format is invalid with error code TXN002
     */
    private LocalDate parseTransactionDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: {}. Expected format: yyyy-MM-dd", dateString);
            throw new BusinessException(
                    ErrorCode.INVALID_TRANSACTION_DATE,
                    "Invalid date format: " + dateString + ". Expected format: yyyy-MM-dd",
                    e).addDetail("providedDate", dateString)
                    .addDetail("expectedFormat", "yyyy-MM-dd");
        }
    }

    /**
     * Validate all transaction items before processing
     * This ensures all products exist and have sufficient stock
     * Throws exception immediately if any validation fails
     *
     * @param dto Transaction request DTO containing items to validate
     * @throws BusinessException if product not found (PRD001) or insufficient stock
     *                           (PRD002)
     */
    private void validateTransactionItems(TransactionRequestDTO dto) {
        log.debug("Validating {} transaction items", dto.getItems().size());

        for (TransactionItemDTO item : dto.getItems()) {
            // Get product (will throw ProductNotFoundException if not found)
            product product = productService.getProductById(item.getProductId());

            // Validate stock (will throw InsufficientStockException if insufficient)
            productService.validateStock(product, item.getQty());

            log.debug("Validated product ID: {}, qty: {}, available stock: {}",
                    item.getProductId(), item.getQty(), product.getCurrentStock());
        }

        log.info("All transaction items validated successfully");
    }

    /**
     * Create transaction history record with initial values
     * Total price is set to zero initially and will be updated after processing all
     * items
     *
     * @param transactionDate Date of the transaction
     * @return Saved transactionHistory entity with generated ID
     */
    private transactionHistory createTransactionHistory(LocalDate transactionDate) {
        transactionHistory transaction = transactionHistory.builder()
                .transactionDate(transactionDate)
                .totalPrice(BigDecimal.ZERO)
                .build();

        return transactionHistoryRepository.save(transaction);
    }

    /**
     * Process a single transaction item
     * This includes: creating transaction detail, stock log, and updating product
     * stock
     * All operations are performed within the same database transaction
     *
     * @param transaction Transaction history entity to associate with
     * @param item        Transaction item DTO containing product ID and quantity
     * @return Total price for this item (quantity × unit price)
     */
    private BigDecimal processTransactionItem(transactionHistory transaction, TransactionItemDTO item) {
        log.info("Processing item: product_id={}, qty={}", item.getProductId(), item.getQty());

        // Step 1: Get product data
        product product = productService.getProductById(item.getProductId());

        log.debug("Product details: id={}, name={}, price={}, current_stock={}",
                product.getId(), product.getProductName(),
                product.getPrice(), product.getCurrentStock());

        // Step 2: Calculate prices
        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQty()));

        // Step 3: Insert transaction detail
        transactionDetail detail = createTransactionDetail(transaction, product, item, unitPrice, totalPrice);
        log.debug("Transaction detail created with ID: {}", detail.getId());

        // Step 4: Insert stock log
        stockLog stockLog = createStockLog(product, item.getQty());
        log.debug("Stock log created with ID: {}", stockLog.getId());

        // Step 5: Update product stock
        productService.updateStock(product, item.getQty());

        return totalPrice;
    }

    /**
     * Create and save transaction detail record
     * Records the product sold, quantity, unit price, and total price
     *
     * @param transaction Transaction history to link with
     * @param product     Product entity being sold
     * @param item        Transaction item DTO with quantity
     * @param unitPrice   Unit price of the product at time of sale
     * @param totalPrice  Calculated total price (quantity × unit price)
     * @return Saved transactionDetail entity with generated ID
     */
    private transactionDetail createTransactionDetail(
            transactionHistory transaction,
            product product,
            TransactionItemDTO item,
            BigDecimal unitPrice,
            BigDecimal totalPrice) {

        transactionDetail detail = transactionDetail.builder()
                .transactionHistory(transaction)
                .product(product)
                .qty(item.getQty())
                .price(unitPrice)
                .totalPrice(totalPrice)
                .build();

        return transactionDetailRepository.save(detail);
    }

    /**
     * Create and save stock log record for sale transaction
     * Records negative quantity change (stock reduction) with log type SALE
     *
     * @param product Product entity whose stock is being reduced
     * @param qty     Quantity sold (will be recorded as negative value)
     * @return Saved stockLog entity with generated ID
     */
    private stockLog createStockLog(product product, Integer qty) {
        stockLog log = stockLog.builder()
                .product(product)
                .quantityChange(-qty) // Negative because it's a sale
                .logType(LOG_TYPE_SALE)
                .build();

        return stockLogRepository.save(log);
    }
}
