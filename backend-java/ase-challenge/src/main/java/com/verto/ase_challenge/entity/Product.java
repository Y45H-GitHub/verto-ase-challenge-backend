package com.verto.ase_challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Min(value = 0, message = "Low stock threshold cannot be negative")
    @Column(nullable = false)
    private Integer lowStockThreshold = 10; // Default threshold
    
    public Product(String name, String description, Integer stockQuantity, Integer lowStockThreshold) {
        this.name = name;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold != null ? lowStockThreshold : 10;
    }
    
    public boolean isLowStock() {
        return stockQuantity != null && lowStockThreshold != null && stockQuantity <= lowStockThreshold;
    }
}