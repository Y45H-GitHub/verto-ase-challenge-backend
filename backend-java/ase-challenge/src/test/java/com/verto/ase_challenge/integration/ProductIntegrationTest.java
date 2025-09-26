package com.verto.ase_challenge.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.verto.ase_challenge.dto.ProductRequest;
import com.verto.ase_challenge.dto.StockUpdateRequest;
import com.verto.ase_challenge.entity.Product;
import com.verto.ase_challenge.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureTestDatabase
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Should perform complete product lifecycle operations")
    void shouldPerformCompleteProductLifecycleOperations() throws Exception {
        // Create product
        ProductRequest createRequest = new ProductRequest("Laptop", "Gaming Laptop", 50, 5);
        
        String createResponse = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.stockQuantity").value(50))
                .andReturn().getResponse().getContentAsString();
        
        // Extract product ID from response
        Long productId = objectMapper.readTree(createResponse).get("id").asLong();
        
        // Get product by ID
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Laptop"));
        
        // Update product
        ProductRequest updateRequest = new ProductRequest("Gaming Laptop Pro", "High-end Gaming Laptop", 60, 8);
        mockMvc.perform(put("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gaming Laptop Pro"))
                .andExpect(jsonPath("$.stockQuantity").value(60));
        
        // Increase stock
        StockUpdateRequest increaseRequest = new StockUpdateRequest(20);
        mockMvc.perform(post("/api/products/" + productId + "/stock/increase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(increaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(80));
        
        // Decrease stock
        StockUpdateRequest decreaseRequest = new StockUpdateRequest(30);
        mockMvc.perform(post("/api/products/" + productId + "/stock/decrease")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decreaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(50));
        
        // Try to decrease stock beyond available amount
        StockUpdateRequest excessiveDecreaseRequest = new StockUpdateRequest(100);
        mockMvc.perform(post("/api/products/" + productId + "/stock/decrease")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(excessiveDecreaseRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient Stock"));
        
        // Verify stock wasn't changed after failed decrease
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(50));
        
        // Delete product
        mockMvc.perform(delete("/api/products/" + productId))
                .andExpect(status().isNoContent());
        
        // Verify product is deleted
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should identify low stock products correctly")
    void shouldIdentifyLowStockProductsCorrectly() throws Exception {
        // Create products with different stock levels
        Product normalStock = new Product("Normal Product", "Normal stock", 100, 10);
        Product lowStock1 = new Product("Low Stock 1", "Low stock product 1", 5, 10);
        Product lowStock2 = new Product("Low Stock 2", "Low stock product 2", 8, 15);
        
        productRepository.save(normalStock);
        productRepository.save(lowStock1);
        productRepository.save(lowStock2);
        
        // Get low stock products
        mockMvc.perform(get("/api/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].lowStock").value(true))
                .andExpect(jsonPath("$[1].lowStock").value(true));
    }
    
    @Test
    @DisplayName("Should handle edge cases for stock operations")
    void shouldHandleEdgeCasesForStockOperations() throws Exception {
        // Create product with minimal stock
        ProductRequest createRequest = new ProductRequest("Edge Case Product", "Testing edge cases", 1, 5);
        
        String createResponse = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        Long productId = objectMapper.readTree(createResponse).get("id").asLong();
        
        // Decrease stock to exactly zero
        StockUpdateRequest decreaseToZero = new StockUpdateRequest(1);
        mockMvc.perform(post("/api/products/" + productId + "/stock/decrease")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decreaseToZero)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(0));
        
        // Try to decrease stock when it's already zero
        StockUpdateRequest decreaseFromZero = new StockUpdateRequest(1);
        mockMvc.perform(post("/api/products/" + productId + "/stock/decrease")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decreaseFromZero)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient Stock"));
        
        // Increase stock from zero
        StockUpdateRequest increaseFromZero = new StockUpdateRequest(10);
        mockMvc.perform(post("/api/products/" + productId + "/stock/increase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(increaseFromZero)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }
    
    @Test
    @DisplayName("Should validate input data correctly")
    void shouldValidateInputDataCorrectly() throws Exception {
        // Test invalid product creation
        ProductRequest invalidProduct = new ProductRequest("", "Description", -5, -1);
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").exists());
        
        // Test invalid stock update
        StockUpdateRequest invalidStockUpdate = new StockUpdateRequest(0);
        
        mockMvc.perform(post("/api/products/1/stock/increase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStockUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }
}