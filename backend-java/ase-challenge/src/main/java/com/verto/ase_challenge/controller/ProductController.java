package com.verto.ase_challenge.controller;

import com.verto.ase_challenge.dto.ProductRequest;
import com.verto.ase_challenge.dto.ProductResponse;
import com.verto.ase_challenge.dto.StockUpdateRequest;
import com.verto.ase_challenge.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("POST /api/products - Creating product: {}", request.getName());
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        log.info("GET /api/products/{} - Fetching product", id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("GET /api/products - Fetching all products");
        List<ProductResponse> response = productService.getAllProducts();
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductRequest request) {
        log.info("PUT /api/products/{} - Updating product", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/stock/increase")
    public ResponseEntity<ProductResponse> increaseStock(
            @PathVariable Long id, 
            @Valid @RequestBody StockUpdateRequest request) {
        log.info("POST /api/products/{}/stock/increase - Increasing stock by {}", id, request.getQuantity());
        ProductResponse response = productService.increaseStock(id, request.getQuantity());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/stock/decrease")
    public ResponseEntity<ProductResponse> decreaseStock(
            @PathVariable Long id, 
            @Valid @RequestBody StockUpdateRequest request) {
        log.info("POST /api/products/{}/stock/decrease - Decreasing stock by {}", id, request.getQuantity());
        ProductResponse response = productService.decreaseStock(id, request.getQuantity());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts() {
        log.info("GET /api/products/low-stock - Fetching low stock products");
        List<ProductResponse> response = productService.getLowStockProducts();
        return ResponseEntity.ok(response);
    }
}