package com.verto.ase_challenge.service;

import com.verto.ase_challenge.dto.ProductRequest;
import com.verto.ase_challenge.dto.ProductResponse;
import com.verto.ase_challenge.entity.Product;
import com.verto.ase_challenge.exception.InsufficientStockException;
import com.verto.ase_challenge.exception.ProductNotFoundException;
import com.verto.ase_challenge.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    private ProductRequest testRequest;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setStockQuantity(100);
        testProduct.setLowStockThreshold(10);
        
        testRequest = new ProductRequest();
        testRequest.setName("Test Product");
        testRequest.setDescription("Test Description");
        testRequest.setStockQuantity(100);
        testRequest.setLowStockThreshold(10);
    }
    
    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        ProductResponse response = productService.createProduct(testRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Product");
        assertThat(response.getStockQuantity()).isEqualTo(100);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should get product by ID successfully")
    void shouldGetProductByIdSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When
        ProductResponse response = productService.getProductById(1L);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Test Product");
        verify(productRepository).findById(1L);
    }
    
    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining("Product not found with ID: 999");
    }
    
    @Test
    @DisplayName("Should get all products successfully")
    void shouldGetAllProductsSuccessfully() {
        // Given
        List<Product> products = Arrays.asList(testProduct, new Product());
        when(productRepository.findAll()).thenReturn(products);
        
        // When
        List<ProductResponse> responses = productService.getAllProducts();
        
        // Then
        assertThat(responses).hasSize(2);
        verify(productRepository).findAll();
    }
    
    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setDescription("Updated Description");
        updateRequest.setStockQuantity(200);
        updateRequest.setLowStockThreshold(20);
        
        // When
        ProductResponse response = productService.updateProduct(1L, updateRequest);
        
        // Then
        assertThat(response).isNotNull();
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }
    
    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When
        productService.deleteProduct(1L);
        
        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).delete(testProduct);
    }
    
    @Test
    @DisplayName("Should increase stock successfully")
    void shouldIncreaseStockSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        ProductResponse response = productService.increaseStock(1L, 50);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(testProduct.getStockQuantity()).isEqualTo(150);
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }
    
    @Test
    @DisplayName("Should decrease stock successfully when sufficient stock available")
    void shouldDecreaseStockSuccessfullyWhenSufficientStock() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        ProductResponse response = productService.decreaseStock(1L, 30);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(testProduct.getStockQuantity()).isEqualTo(70);
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }
    
    @Test
    @DisplayName("Should throw exception when trying to decrease stock below zero")
    void shouldThrowExceptionWhenDecreasingStockBelowZero() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        assertThatThrownBy(() -> productService.decreaseStock(1L, 150))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient stock. Available: 100, Requested: 150");
        
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should throw exception when trying to decrease exact stock amount plus one")
    void shouldThrowExceptionWhenDecreasingExactStockPlusOne() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        assertThatThrownBy(() -> productService.decreaseStock(1L, 101))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient stock. Available: 100, Requested: 101");
    }
    
    @Test
    @DisplayName("Should allow decreasing stock to exactly zero")
    void shouldAllowDecreasingStockToExactlyZero() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        ProductResponse response = productService.decreaseStock(1L, 100);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(testProduct.getStockQuantity()).isEqualTo(0);
        verify(productRepository).save(testProduct);
    }
    
    @Test
    @DisplayName("Should get low stock products successfully")
    void shouldGetLowStockProductsSuccessfully() {
        // Given
        Product lowStockProduct = new Product();
        lowStockProduct.setId(2L);
        lowStockProduct.setName("Low Stock Product");
        lowStockProduct.setStockQuantity(5);
        lowStockProduct.setLowStockThreshold(10);
        
        List<Product> lowStockProducts = Arrays.asList(lowStockProduct);
        when(productRepository.findLowStockProducts()).thenReturn(lowStockProducts);
        
        // When
        List<ProductResponse> responses = productService.getLowStockProducts();
        
        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).isLowStock()).isTrue();
        verify(productRepository).findLowStockProducts();
    }
    
    @Test
    @DisplayName("Should handle edge case - decrease stock by 1 when stock is 1")
    void shouldHandleEdgeCaseDecreaseStockBy1WhenStockIs1() {
        // Given
        testProduct.setStockQuantity(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        ProductResponse response = productService.decreaseStock(1L, 1);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(testProduct.getStockQuantity()).isEqualTo(0);
        verify(productRepository).save(testProduct);
    }
    
    @Test
    @DisplayName("Should handle edge case - try to decrease stock by 1 when stock is 0")
    void shouldHandleEdgeCaseTryDecreaseStockBy1WhenStockIs0() {
        // Given
        testProduct.setStockQuantity(0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        assertThatThrownBy(() -> productService.decreaseStock(1L, 1))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient stock. Available: 0, Requested: 1");
    }
}