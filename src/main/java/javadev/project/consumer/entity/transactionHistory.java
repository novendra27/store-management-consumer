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
