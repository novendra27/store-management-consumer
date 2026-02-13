package javadev.project.consumer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import javadev.project.consumer.dto.TransactionRequestDTO;
import javadev.project.consumer.entity.transactionHistory;
import javadev.project.consumer.exception.BusinessException;
import javadev.project.consumer.exception.ErrorCode;
import javadev.project.consumer.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for sales transaction messages
 * Listens to configured Kafka topic and processes incoming sales transaction
 * JSON messages
 * Validates, parses, and delegates transaction processing to TransactionService
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SalesTransactionConsumer {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    /**
     * Kafka listener for sales transaction topic
     */
    @KafkaListener(topics = "${kafka.topic.sales-transaction}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        log.info("========================================");
        log.info("Received Kafka message from topic: sales-transaction-adi");

        // Quick validation: Check if message looks like JSON
        if (!isValidJsonFormat(message)) {
            log.warn("⚠️ Skipped non-JSON message: {}",
                    message.length() > 100 ? message.substring(0, 100) + "..." : message);
            return;
        }

        log.info("Message: {}", message);
        log.info("========================================");

        try {
            // Step 1: Parse JSON to DTO
            TransactionRequestDTO request = parseMessage(message);
            log.info("✓ Parsed message: date={}, items={}",
                    request.getTransactionDate(),
                    request.getItems().size());

            // Step 2: Process transaction
            transactionHistory transaction = transactionService.processTransaction(request);
            log.info("✓ SUCCESS - Transaction processed: id={}, totalPrice={}",
                    transaction.getId(),
                    transaction.getTotalPrice());

        } catch (BusinessException e) {
            log.error("✗ Business Error [{}]: {}",
                    e.getErrorCode().getCode(),
                    e.getMessage());
            // Transaction rolled back automatically

        } catch (Exception e) {
            log.error("✗ System Error: {} - Cause: {}",
                    e.getMessage(),
                    e.getCause() != null ? e.getCause().getMessage() : "Unknown");
        }
    }

    /**
     * Quick validation to check if message looks like JSON
     */
    private boolean isValidJsonFormat(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        String trimmed = message.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }

    /**
     * Parse JSON message string to TransactionRequestDTO
     * Converts JSON message to DTO object using ObjectMapper
     *
     * @param message Raw JSON message string from Kafka
     * @return TransactionRequestDTO object
     * @throws BusinessException if JSON parsing fails with error code KFK001
     */
    private TransactionRequestDTO parseMessage(String message) {
        try {
            return objectMapper.readValue(message, TransactionRequestDTO.class);
        } catch (Exception e) {
            // Clean error log without stack trace
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 200) {
                errorMsg = errorMsg.substring(0, 200) + "...";
            }
            throw new BusinessException(
                    ErrorCode.KAFKA_MESSAGE_PARSING_ERROR,
                    "Invalid JSON format: " + errorMsg)
                    .addDetail("rawMessage", message.length() > 100 ? message.substring(0, 100) + "..." : message);
        }
    }
}
