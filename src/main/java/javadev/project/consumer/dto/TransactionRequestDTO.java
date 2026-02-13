package javadev.project.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for incoming transaction request from Kafka
 * Contains transaction date and list of items to be sold
 * Supports flexible date format via custom deserializer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequestDTO {

    @NotNull(message = "Transaction date cannot be null")
    @JsonProperty("transaction_date")
    @JsonDeserialize(using = TransactionDateDeserializer.class)
    private String transactionDate;

    @NotNull(message = "Items cannot be null")
    @NotEmpty(message = "Items cannot be empty")
    @Valid
    @JsonProperty("items")
    private List<TransactionItemDTO> items;
}
