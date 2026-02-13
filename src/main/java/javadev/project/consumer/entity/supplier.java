package javadev.project.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class representing a supplier/vendor
 * Maps to the 'supplier' table in the database
 * Contains supplier contact information and business details
 * 
 * Maintains bidirectional relationship with Product entity
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
@Table(name = "supplier")
public class supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    @Column(name = "contact", length = 20)
    private String contact;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "supplier")
    private List<product> products;
}
