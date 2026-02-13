package javadev.project.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class representing a product in the inventory system
 * Maps to the 'product' table in the database
 * Contains product information, pricing, stock levels, and relationships to
 * category and supplier
 * 
 * Uses Hibernate annotations for automatic timestamp generation (created_at,
 * updated_at)
 * Lombok annotations provide getters, setters, constructors, and builder
 * pattern
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku", length = 50)
    private String sku;

    @Column(name = "product_name", length = 100)
    private String productName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private category category;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private supplier supplier;

    @Column(name = "current_stock")
    private Integer currentStock;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product")
    private List<transactionDetail> transactionDetails;

    @OneToMany(mappedBy = "product")
    private List<stockLog> stockLogs;
}
