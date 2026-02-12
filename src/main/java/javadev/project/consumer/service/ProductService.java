package javadev.project.consumer.service;

import javadev.project.consumer.entity.product;
import javadev.project.consumer.exception.BusinessException;
import javadev.project.consumer.exception.ErrorCode;
import javadev.project.consumer.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Get product by ID
     * 
     * @param productId Product ID
     * @return Product entity
     * @throws BusinessException if product not found
     */
    public product getProductById(Integer productId) {
        log.debug("Fetching product with ID: {}", productId);
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with ID: " + productId).addDetail("productId", productId));
    }

    /**
     * Validate product stock availability
     * 
     * @param product     Product entity
     * @param requiredQty Required quantity
     * @throws BusinessException if stock is insufficient
     */
    public void validateStock(product product, Integer requiredQty) {
        if (product.getCurrentStock() < requiredQty) {
            log.warn("Insufficient stock for product ID: {}. Required: {}, Available: {}",
                    product.getId(), requiredQty, product.getCurrentStock());
            throw new BusinessException(
                    ErrorCode.INSUFFICIENT_STOCK,
                    String.format("Insufficient stock for product ID %d. Required: %d, Available: %d",
                            product.getId(), requiredQty, product.getCurrentStock()))
                    .addDetail("productId", product.getId())
                    .addDetail("productName", product.getProductName())
                    .addDetail("requiredQty", requiredQty)
                    .addDetail("availableStock", product.getCurrentStock());
        }
    }

    /**
     * Update product stock (reduce for sale)
     * 
     * @param product Product entity
     * @param qty     Quantity to reduce
     */
    @Transactional
    public void updateStock(product product, Integer qty) {
        Integer currentStock = product.getCurrentStock();
        Integer newStock = currentStock - qty;

        product.setCurrentStock(newStock);
        productRepository.save(product);

        log.info("Updated stock for product ID: {}. Old stock: {}, New stock: {}",
                product.getId(), currentStock, newStock);

        // Warning if stock is low
        if (newStock < 10) {
            log.warn("Low stock alert for product ID: {} ({}). Current stock: {}",
                    product.getId(), product.getProductName(), newStock);
        }
    }
}
