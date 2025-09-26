import { Database } from '../database/Database';
import { Product, CreateProductRequest, UpdateProductRequest, StockUpdateRequest } from '../types/Product';

export class ProductService {
    constructor(private database: Database) { }

    async getAllProducts(): Promise<Product[]> {
        console.log('Fetching all products');
        return await this.database.getAllProducts();
    }

    async getProductById(id: number): Promise<Product> {
        console.log(`Fetching product with ID: ${id}`);
        const product = await this.database.getProductById(id);
        if (!product) {
            throw new Error(`Product not found with ID: ${id}`);
        }
        return product;
    }

    async createProduct(request: CreateProductRequest): Promise<Product> {
        console.log(`Creating product with name: ${request.name}`);

        // Validate input
        if (!request.name || request.name.trim().length === 0) {
            throw new Error('Product name is required');
        }

        if (request.stockQuantity < 0) {
            throw new Error('Stock quantity cannot be negative');
        }

        if (request.lowStockThreshold !== undefined && request.lowStockThreshold < 0) {
            throw new Error('Low stock threshold cannot be negative');
        }

        const productData = {
            name: request.name.trim(),
            description: request.description?.trim(),
            stockQuantity: request.stockQuantity,
            lowStockThreshold: request.lowStockThreshold || 10
        };

        const product = await this.database.createProduct(productData);
        console.log(`Product created with ID: ${product.id}`);
        return product;
    }

    async updateProduct(id: number, request: UpdateProductRequest): Promise<Product> {
        console.log(`Updating product with ID: ${id}`);

        // Validate input
        if (request.name !== undefined && (!request.name || request.name.trim().length === 0)) {
            throw new Error('Product name cannot be empty');
        }

        if (request.stockQuantity !== undefined && request.stockQuantity < 0) {
            throw new Error('Stock quantity cannot be negative');
        }

        if (request.lowStockThreshold !== undefined && request.lowStockThreshold < 0) {
            throw new Error('Low stock threshold cannot be negative');
        }

        const updates: Partial<Omit<Product, 'id' | 'lowStock'>> = {};

        if (request.name !== undefined) {
            updates.name = request.name.trim();
        }
        if (request.description !== undefined) {
            updates.description = request.description?.trim();
        }
        if (request.stockQuantity !== undefined) {
            updates.stockQuantity = request.stockQuantity;
        }
        if (request.lowStockThreshold !== undefined) {
            updates.lowStockThreshold = request.lowStockThreshold;
        }

        const product = await this.database.updateProduct(id, updates);
        if (!product) {
            throw new Error(`Product not found with ID: ${id}`);
        }

        console.log(`Product updated with ID: ${id}`);
        return product;
    }

    async deleteProduct(id: number): Promise<void> {
        console.log(`Deleting product with ID: ${id}`);
        const deleted = await this.database.deleteProduct(id);
        if (!deleted) {
            throw new Error(`Product not found with ID: ${id}`);
        }
        console.log(`Product deleted with ID: ${id}`);
    }

    async increaseStock(id: number, request: StockUpdateRequest): Promise<Product> {
        console.log(`Increasing stock for product ID: ${id} by quantity: ${request.quantity}`);

        if (request.quantity <= 0) {
            throw new Error('Quantity must be greater than 0');
        }

        const product = await this.getProductById(id);
        const newStockQuantity = product.stockQuantity + request.quantity;

        const updatedProduct = await this.database.updateProduct(id, { stockQuantity: newStockQuantity });
        if (!updatedProduct) {
            throw new Error(`Product not found with ID: ${id}`);
        }

        console.log(`Stock increased for product ID: ${id}. New stock: ${updatedProduct.stockQuantity}`);
        return updatedProduct;
    }

    async decreaseStock(id: number, request: StockUpdateRequest): Promise<Product> {
        console.log(`Decreasing stock for product ID: ${id} by quantity: ${request.quantity}`);

        if (request.quantity <= 0) {
            throw new Error('Quantity must be greater than 0');
        }

        const product = await this.getProductById(id);

        if (product.stockQuantity < request.quantity) {
            const errorMessage = `Insufficient stock. Available: ${product.stockQuantity}, Requested: ${request.quantity}`;
            console.error(`Insufficient stock for product ID: ${id}. ${errorMessage}`);
            throw new Error(errorMessage);
        }

        const newStockQuantity = product.stockQuantity - request.quantity;
        const updatedProduct = await this.database.updateProduct(id, { stockQuantity: newStockQuantity });
        if (!updatedProduct) {
            throw new Error(`Product not found with ID: ${id}`);
        }

        console.log(`Stock decreased for product ID: ${id}. New stock: ${updatedProduct.stockQuantity}`);
        return updatedProduct;
    }

    async getLowStockProducts(): Promise<Product[]> {
        console.log('Fetching products with low stock');
        return await this.database.getLowStockProducts();
    }
}