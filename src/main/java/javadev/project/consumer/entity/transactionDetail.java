package javadev.project.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing individual line items in a sales transaction
 * Maps to the 'transaction_detail' table in the database
 * Contains product information, quantity, unit price, and total price for each
 * item
 * 
 * Maintains many-to-one relationships with TransactionHistory and Product
 * entities
 * Total price is calculated as quantity × unit price
 * Uses Hibernate annotation for automatic timestamp generation (created_at)
 * Lombok annotations provide getters, setters, constructors, and builder
 * pattern
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transaction_detail")
public class transactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private transactionHistory transactionHistory;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private product product;

    @Column(name = "qty")
    private Integer qty;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
