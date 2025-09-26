package com.verto.ase_challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.verto.ase_challenge.dto.ProductRequest;
import com.verto.ase_challenge.dto.ProductResponse;
import com.verto.ase_challenge.dto.StockUpdateRequest;
import com.verto.ase_challenge.exception.InsufficientStockException;
import com.verto.ase_challenge.exception.ProductNotFoundException;
import com.verto.ase_challenge.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private ProductResponse testProductResponse;
    private ProductRequest testProductRequest;
    
    @BeforeEach
    void setUp() {
        testProductResponse = new ProductResponse();
        testProductResponse.setId(1L);
        testProductResponse.setName("Test Product");
        testProductResponse.setDescription("Test Description");
        testProductResponse.setStockQuantity(100);
        testProductResponse.setLowStockThreshold(10);
        testProductResponse.setLowStock(false);
        
        testProductRequest = new ProductRequest();
        testProductRequest.setName("Test Product");
        testProductRequest.setDescription("Test Description");
        testProductRequest.setStockQuantity(100);
        testProductRequest.setLowStockThreshold(10);
    }
    
    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        // Given
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(testProductResponse);
        
        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.stockQuantity").value(100));
        
        verify(productService).createProduct(any(ProductRequest.class));
    }
    
    @Test
    @DisplayName("Should return validation error for invalid product request")
    void shouldReturnValidationErrorForInvalidProductRequest() throws Exception {
        // Given
        ProductRequest invalidRequest = new ProductRequest();
        invalidRequest.setName(""); // Invalid - blank name
        invalidRequest.setStockQuantity(-1); // Invalid - negative stock
        
        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));
        
        verify(productService, never()).createProduct(any(ProductRequest.class));
    }
    
    @Test
    @DisplayName("Should get product by ID successfully")
    void shouldGetProductByIdSuccessfully() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(testProductResponse);
        
        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
        
        verify(productService).getProductById(1L);
    }
    
    @Test
    @DisplayName("Should return 404 when product not found")
    void shouldReturn404WhenProductNotFound() throws Exception {
        // Given
        when(productService.getProductById(999L)).thenThrow(new ProductNotFoundException("Product not found with ID: 999"));
        
        // When & Then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Product Not Found"));
        
        verify(productService).getProductById(999L);
    }
    
    @Test
    @DisplayName("Should get all products successfully")
    void shouldGetAllProductsSuccessfully() throws Exception {
        // Given
        List<ProductResponse> products = Arrays.asList(testProductResponse);
        when(productService.getAllProducts()).thenReturn(products);
        
        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
        
        verify(productService).getAllProducts();
    }
    
    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        // Given
        when(productService.updateProduct(anyLong(), any(ProductRequest.class))).thenReturn(testProductResponse);
        
        // When & Then
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        
        verify(productService).updateProduct(eq(1L), any(ProductRequest.class));
    }
    
    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
        
        verify(productService).deleteProduct(1L);
    }
    
    @Test
    @DisplayName("Should increase stock successfully")
    void shouldIncreaseStockSuccessfully() throws Exception {
        // Given
        StockUpdateRequest request = new StockUpdateRequest(50);
        testProductResponse.setStockQuantity(150);
        when(productService.increaseStock(1L, 50)).thenReturn(testProductResponse);
        
        // When & Then
        mockMvc.perform(post("/api/products/1/stock/increase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(150));
        
        verify(productService).increaseStock(1L, 50);
    }
    
    @Test
    @DisplayName("Should decrease stock successfully")
    void shouldDecreaseStockSuccessfully() throws Exception {
        // Given
        StockUpdateRequest request = new StockUpdateRequest(30);
        testProductResponse.setStockQuantity(70);
        when(productService.decreaseStock(1L, 30)).thenReturn(testProductResponse);
        
        // When & Then
        mockMvc.perform(post("/api/products/1/stock/decrease")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(70));
        
        verify(productService).decreaseStock(1L, 30);
    }
    
    @Test
    @DisplayName("Should return 400 when insufficient stock for decrease")
    void shouldReturn400WhenInsufficientStockForDecrease() throws Exception {
        // Given
        StockUpdateRequest request = new StockUpdateRequest(150);
        when(productService.decreaseStock(1L, 150))
            .thenThrow(new InsufficientStockException("Insufficient stock. Available: 100, Requested: 150"));
        
        // When & Then
        mockMvc.perform(post("/api/products/1/stock/decrease")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Insufficient Stock"));
        
        verify(productService).decreaseStock(1L, 150);
    }
    
    @Test
    @DisplayName("Should return validation error for invalid stock update request")
    void shouldReturnValidationErrorForInvalidStockUpdateRequest() throws Exception {
        // Given
        StockUpdateRequest invalidRequest = new StockUpdateRequest(0); // Invalid - must be at least 1
        
        // When & Then
        mockMvc.perform(post("/api/products/1/stock/increase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));
        
        verify(productService, never()).increaseStock(anyLong(), anyInt());
    }
    
    @Test
    @DisplayName("Should get low stock products successfully")
    void shouldGetLowStockProductsSuccessfully() throws Exception {
        // Given
        ProductResponse lowStockProduct = new ProductResponse();
        lowStockProduct.setId(2L);
        lowStockProduct.setName("Low Stock Product");
        lowStockProduct.setStockQuantity(5);
        lowStockProduct.setLowStockThreshold(10);
        lowStockProduct.setLowStock(true);
        
        List<ProductResponse> lowStockProducts = Arrays.asList(lowStockProduct);
        when(productService.getLowStockProducts()).thenReturn(lowStockProducts);
        
        // When & Then
        mockMvc.perform(get("/api/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].lowStock").value(true));
        
        verify(productService).getLowStockProducts();
    }
}