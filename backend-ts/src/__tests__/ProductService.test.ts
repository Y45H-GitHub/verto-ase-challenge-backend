import { Database } from '../database/Database';
import { ProductService } from '../services/ProductService';
import { Product } from '../types/Product';

describe('ProductService', () => {
    let database: Database;
    let productService: ProductService;

    beforeEach(async () => {
        database = new Database(':memory:');
        await database.waitForInitialization();
        productService = new ProductService(database);
    });

    afterEach(() => {
        database.close();
    });

    describe('getAllProducts', () => {
        it('should return all products', async () => {
            const products = await productService.getAllProducts();
            expect(products).toHaveLength(6); // Sample data
            expect(products[0]).toHaveProperty('id');
            expect(products[0]).toHaveProperty('name');
            expect(products[0]).toHaveProperty('stockQuantity');
        });
    });

    describe('getProductById', () => {
        it('should return a product by ID', async () => {
            const product = await productService.getProductById(1);
            expect(product).toHaveProperty('id', 1);
            expect(product).toHaveProperty('name');
        });

        it('should throw error for non-existent product', async () => {
            await expect(productService.getProductById(999)).rejects.toThrow('Product not found with ID: 999');
        });
    });

    describe('createProduct', () => {
        it('should create a new product', async () => {
            const request = {
                name: 'Test Product',
                description: 'Test Description',
                stockQuantity: 100,
                lowStockThreshold: 10
            };

            const product = await productService.createProduct(request);
            expect(product).toHaveProperty('id');
            expect(product.name).toBe('Test Product');
            expect(product.stockQuantity).toBe(100);
            expect(product.lowStockThreshold).toBe(10);
        });

        it('should throw error for invalid product name', async () => {
            const request = {
                name: '',
                stockQuantity: 100
            };

            await expect(productService.createProduct(request)).rejects.toThrow('Product name is required');
        });

        it('should throw error for negative stock quantity', async () => {
            const request = {
                name: 'Test Product',
                stockQuantity: -1
            };

            await expect(productService.createProduct(request)).rejects.toThrow('Stock quantity cannot be negative');
        });
    });

    describe('updateProduct', () => {
        it('should update an existing product', async () => {
            const updates = {
                name: 'Updated Product',
                stockQuantity: 150
            };

            const product = await productService.updateProduct(1, updates);
            expect(product.name).toBe('Updated Product');
            expect(product.stockQuantity).toBe(150);
        });

        it('should throw error for non-existent product', async () => {
            const updates = { name: 'Updated Product' };
            await expect(productService.updateProduct(999, updates)).rejects.toThrow('Product not found with ID: 999');
        });
    });

    describe('deleteProduct', () => {
        it('should delete an existing product', async () => {
            await expect(productService.deleteProduct(1)).resolves.not.toThrow();
            await expect(productService.getProductById(1)).rejects.toThrow('Product not found with ID: 1');
        });

        it('should throw error for non-existent product', async () => {
            await expect(productService.deleteProduct(999)).rejects.toThrow('Product not found with ID: 999');
        });
    });

    describe('increaseStock', () => {
        it('should increase stock quantity', async () => {
            const request = { quantity: 50 };
            const originalProduct = await productService.getProductById(1);
            const updatedProduct = await productService.increaseStock(1, request);

            expect(updatedProduct.stockQuantity).toBe(originalProduct.stockQuantity + 50);
        });

        it('should throw error for invalid quantity', async () => {
            const request = { quantity: 0 };
            await expect(productService.increaseStock(1, request)).rejects.toThrow('Quantity must be greater than 0');
        });
    });

    describe('decreaseStock', () => {
        it('should decrease stock quantity', async () => {
            const request = { quantity: 10 };
            const originalProduct = await productService.getProductById(1);
            const updatedProduct = await productService.decreaseStock(1, request);

            expect(updatedProduct.stockQuantity).toBe(originalProduct.stockQuantity - 10);
        });

        it('should throw error for insufficient stock', async () => {
            const request = { quantity: 1000 };
            await expect(productService.decreaseStock(1, request)).rejects.toThrow('Insufficient stock');
        });

        it('should throw error for invalid quantity', async () => {
            const request = { quantity: 0 };
            await expect(productService.decreaseStock(1, request)).rejects.toThrow('Quantity must be greater than 0');
        });
    });

    describe('getLowStockProducts', () => {
        it('should return products with low stock', async () => {
            const lowStockProducts = await productService.getLowStockProducts();
            expect(Array.isArray(lowStockProducts)).toBe(true);

            // All returned products should have lowStock = true
            lowStockProducts.forEach(product => {
                expect(product.lowStock).toBe(true);
                expect(product.stockQuantity).toBeLessThanOrEqual(product.lowStockThreshold);
            });
        });
    });
});