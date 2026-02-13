package javadev.project.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class representing a sales transaction header
 * Maps to the 'transaction_history' table in the database
 * Contains transaction-level information: date, total price, and status
 * 
 * Maintains one-to-many relationship with TransactionDetail entity
 * Total price is calculated as sum of all transaction detail prices
 * Uses Hibernate annotation for automatic timestamp generation (created_at)
 * Lombok annotations provide getters, setters, constructors, and builder
 * pattern
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transaction_history")
public class transactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "transactionHistory", cascade = CascadeType.ALL)
    private List<transactionDetail> transactionDetails;
}
