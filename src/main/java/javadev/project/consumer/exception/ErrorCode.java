package javadev.project.consumer.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Product related errors
    PRODUCT_NOT_FOUND("PRD001", "Product not found"),
    INSUFFICIENT_STOCK("PRD002", "Insufficient stock"),

    // Transaction related errors
    TRANSACTION_PROCESSING_ERROR("TXN001", "Transaction processing failed"),
    INVALID_TRANSACTION_DATE("TXN002", "Invalid transaction date format"),
    INVALID_TRANSACTION_DATA("TXN003", "Invalid transaction data"),
    EMPTY_TRANSACTION_ITEMS("TXN004", "Transaction items cannot be empty"),

    // Validation errors
    VALIDATION_ERROR("VAL001", "Validation error"),
    INVALID_QUANTITY("VAL002", "Invalid quantity"),

    // Kafka related errors
    KAFKA_MESSAGE_PARSING_ERROR("KFK001", "Failed to parse Kafka message"),
    KAFKA_CONSUMER_ERROR("KFK002", "Kafka consumer error"),

    // Database errors
    DATABASE_ERROR("DB001", "Database operation failed");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
