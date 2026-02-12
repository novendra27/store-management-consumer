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
        log.info("Received message from Kafka");
        log.info("Topic: sales-transaction-adi");
        log.info("Message: {}", message);
        log.info("========================================");

        try {
            // Step 1: Parse JSON to DTO
            TransactionRequestDTO request = parseMessage(message);
            log.info("Successfully parsed message: date={}, items={}",
                    request.getTransactionDate(),
                    request.getItems().size());

            // Step 2: Process transaction
            transactionHistory transaction = transactionService.processTransaction(request);
            log.info("SUCCESS - Transaction processed: id={}, totalPrice={}",
                    transaction.getId(),
                    transaction.getTotalPrice());

        } catch (BusinessException e) {
            log.error("ERROR - Business error: [{}] {}",
                    e.getErrorCode().getCode(),
                    e.getDetailedMessage());
            // Transaction rolled back automatically

        } catch (Exception e) {
            log.error("ERROR - Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process message", e);
        }
    }

    private TransactionRequestDTO parseMessage(String message) {
        try {
            return objectMapper.readValue(message, TransactionRequestDTO.class);
        } catch (Exception e) {
            log.error("Failed to parse message: {}", message, e);
            throw new BusinessException(
                    ErrorCode.KAFKA_MESSAGE_PARSING_ERROR,
                    "Failed to parse Kafka message",
                    e).addDetail("rawMessage", message);
        }
    }
}
