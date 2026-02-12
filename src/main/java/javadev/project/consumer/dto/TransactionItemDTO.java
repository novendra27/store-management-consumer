package javadev.project.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionItemDTO {

    @NotNull(message = "Product ID cannot be null")
    @JsonProperty("product_id")
    private Integer productId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    @JsonProperty("qty")
    private Integer qty;
}
