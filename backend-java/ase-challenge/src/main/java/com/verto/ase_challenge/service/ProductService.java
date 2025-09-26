package com.verto.ase_challenge.service;

import com.verto.ase_challenge.dto.ProductRequest;
import com.verto.ase_challenge.dto.ProductResponse;
import com.verto.ase_challenge.entity.Product;
import com.verto.ase_challenge.exception.InsufficientStockException;
import com.verto.ase_challenge.exception.ProductNotFoundException;
import com.verto.ase_challenge.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with name: {}", request.getName());
        
        Product product = new Product(
            request.getName(),
            request.getDescription(),
            request.getStockQuantity(),
            request.getLowStockThreshold()
        );
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());
        
        return ProductResponse.fromEntity(savedProduct);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        Product product = findProductById(id);
        return ProductResponse.fromEntity(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll()
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);
        Product product = findProductById(id);
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setStockQuantity(request.getStockQuantity());
        product.setLowStockThreshold(request.getLowStockThreshold());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated with ID: {}", updatedProduct.getId());
        
        return ProductResponse.fromEntity(updatedProduct);
    }
    
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        Product product = findProductById(id);
        productRepository.delete(product);
        log.info("Product deleted with ID: {}", id);
    }
    
    public ProductResponse increaseStock(Long id, Integer quantity) {
        log.info("Increasing stock for product ID: {} by quantity: {}", id, quantity);
        Product product = findProductById(id);
        
        int newStock = product.getStockQuantity() + quantity;
        product.setStockQuantity(newStock);
        
        Product updatedProduct = productRepository.save(product);
        log.info("Stock increased for product ID: {}. New stock: {}", id, newStock);
        
        return ProductResponse.fromEntity(updatedProduct);
    }
    
    public ProductResponse decreaseStock(Long id, Integer quantity) {
        log.info("Decreasing stock for product ID: {} by quantity: {}", id, quantity);
        Product product = findProductById(id);
        
        if (product.getStockQuantity() < quantity) {
            String message = String.format(
                "Insufficient stock. Available: %d, Requested: %d", 
                product.getStockQuantity(), 
                quantity
            );
            log.error("Insufficient stock for product ID: {}. {}", id, message);
            throw new InsufficientStockException(message);
        }
        
        int newStock = product.getStockQuantity() - quantity;
        product.setStockQuantity(newStock);
        
        Product updatedProduct = productRepository.save(product);
        log.info("Stock decreased for product ID: {}. New stock: {}", id, newStock);
        
        return ProductResponse.fromEntity(updatedProduct);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        log.info("Fetching products with low stock");
        return productRepository.findLowStockProducts()
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    private Product findProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }
}