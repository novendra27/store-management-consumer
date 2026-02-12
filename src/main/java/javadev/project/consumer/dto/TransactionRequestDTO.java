package javadev.project.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequestDTO {

    @NotNull(message = "Transaction date cannot be null")
    @JsonProperty("transaction_date")
    private String transactionDate;

    @NotNull(message = "Items cannot be null")
    @NotEmpty(message = "Items cannot be empty")
    @Valid
    @JsonProperty("items")
    private List<TransactionItemDTO> items;
}
