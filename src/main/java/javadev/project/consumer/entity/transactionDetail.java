package javadev.project.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
