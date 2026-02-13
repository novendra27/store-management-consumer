package javadev.project.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing stock movement history
 * Maps to the 'stock_log' table in the database
 * Tracks all stock changes: quantity before, quantity after, and change amount
 * 
 * Maintains many-to-one relationship with Product entity
 * Used for audit trail and inventory reconciliation
 * Uses Hibernate annotation for automatic timestamp generation (created_at)
 * Lombok annotations provide getters, setters, constructors, and builder
 * pattern
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "stock_log")
public class stockLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private product product;

    @Column(name = "quantity_change")
    private Integer quantityChange;

    @Column(name = "log_type", length = 20)
    private String logType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
