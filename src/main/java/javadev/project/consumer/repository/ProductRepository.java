package javadev.project.consumer.repository;

import javadev.project.consumer.entity.product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Product entity
 * Provides CRUD operations and custom queries for product management
 */
@Repository
public interface ProductRepository extends JpaRepository<product, Integer> {

    /**
     * Find product by ID
     * 
     * @param id Product ID
     * @return Optional of product
     */
    Optional<product> findById(Integer id);

    /**
     * Find product by SKU
     * 
     * @param sku Product SKU
     * @return Optional of product
     */
    Optional<product> findBySku(String sku);
}
