package javadev.project.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
