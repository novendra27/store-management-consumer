package javadev.project.consumer.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for individual transaction item
 * Contains product ID and quantity for a single line item in the transaction
 * Supports both camelCase (productId) and snake_case (product_id) JSON field
 * names
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionItemDTO {

    @NotNull(message = "Product ID cannot be null")
    @JsonProperty("product_id")
    @JsonAlias({ "productId", "product_id" })
    private Integer productId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    @JsonProperty("qty")
    private Integer qty;
}
