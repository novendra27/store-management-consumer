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
