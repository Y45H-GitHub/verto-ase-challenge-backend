package com.verto.ase_challenge.dto;

import com.verto.ase_challenge.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private Long id;
    private String name;
    private String description;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private boolean isLowStock;
    
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getStockQuantity(),
            product.getLowStockThreshold(),
            product.isLowStock()
        );
    }
}