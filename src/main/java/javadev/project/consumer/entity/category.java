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
 * Entity class representing a product category
 * Maps to the 'category' table in the database
 * Used to organize products into logical groups (e.g., Electronics, Clothing,
 * Food)
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
@Table(name = "category")
public class category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_name", length = 50)
    private String categoryName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category")
    private List<product> products;
}
