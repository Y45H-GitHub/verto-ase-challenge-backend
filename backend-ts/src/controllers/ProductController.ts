import { Request, Response } from 'express';
import { ProductService } from '../services/ProductService';
import { CreateProductRequest, UpdateProductRequest, StockUpdateRequest } from '../types/Product';

export class ProductController {
    constructor(private productService: ProductService) { }

    getAllProducts = async (req: Request, res: Response): Promise<void> => {
        try {
            console.log('GET /api/products - Fetching all products');
            const products = await this.productService.getAllProducts();
            res.json(products);
        } catch (error) {
            console.error('Error fetching products:', error);
            res.status(500).json({
                status: 500,
                error: 'Internal Server Error',
                message: 'Failed to fetch products',
                timestamp: new Date().toISOString()
            });
        }
    };

    getProductById = async (req: Request, res: Response): Promise<void> => {
        try {
            const id = parseInt(req.params.id);
            if (isNaN(id)) {
                res.status(400).json({
                    status: 400,
                    error: 'Bad Request',
                    message: 'Invalid product ID',
                    timestamp: new Date().toISOString()
                });
                return;
            }

            console.log(`GET /api/products/${id} - Fetching product`);
            const product = await this.productService.getProductById(id);
            res.json(product);
        } catch (error) {
            console.error('Error fetching product:', error);
            if (error instanceof Error && error.message.includes('not found')) {
                res.status(404).json({
                    status: 404,
                    error: 'Product Not Found',
                    message: error.message,
                    timestamp: new Date().toISOString()
                });
            } else {
                res.status(500).json({
                    status: 500,
                    error: 'Internal Server Error',
                    message: 'Failed to fetch product',
                    timestamp: new Date().toISOString()
                });
            }
        }
    };

    createProduct = async (req: Request, res: Response): Promise<void> => {
        try {
            const request: CreateProductRequest = req.body;

            // Validate request
            const validationErrors: Record<string, string> = {};

            if (!request.name || request.name.trim().length === 0) {
                validationErrors.name = 'Product name is required';
            }

            if (request.stockQuantity === undefined || request.stockQuantity < 0) {
                validationErrors.stockQuantity = 'Stock quantity cannot be negative';
            }

            if (request.lowStockThreshold !== undefined && request.lowStockThreshold < 0) {
                validationErrors.lowStockThreshold = 'Low stock threshold cannot be negative';
            }

            if (Object.keys(validationErrors).length > 0) {
                res.status(400).json({
                    status: 400,
                    error: 'Validation Failed',
                    message: 'Invalid input data',
                    validationErrors,
                    timestamp: new Date().toISOString()
                });
                return;
            }

            console.log(`POST /api/products - Creating product: ${request.name}`);
            const product = await this.productService.createProduct(request);
            res.status(201).json(product);
        } catch (error) {
            console.error('Error creating product:', error);
            res.status(500).json({
                status: 500,
                error: 'Internal Server Error',
                message: 'Failed to create product',
                timestamp: new Date().toISOString()
            });
        }
    };

    updateProduct = async (req: Request, res: Response): Promise<void> => {
        try {
            const id = parseInt(req.params.id);
            if (isNaN(id)) {
                res.status(400).json({
                    status: 400,
                    error: 'Bad Request',
                    message: 'Invalid product ID',
                    timestamp: new Date().toISOString()
                });
                return;
            }

            const request: UpdateProductRequest = req.body;

            // Validate request
            const validationErrors: Record<string, string> = {};

            if (request.name !== undefined && (!request.name || request.name.trim().length === 0)) {
                validationErrors.name = 'Product name cannot be empty';
            }

            if (request.stockQuantity !== undefined && request.stockQuantity < 0) {
                validationErrors.stockQuantity = 'Stock quantity cannot be negative';
            }

            if (request.lowStockThreshold !== undefined && request.lowStockThreshold < 0) {
                validationErrors.lowStockThreshold = 'Low stock threshold cannot be negative';
            }

            if (Object.keys(validationErrors).length > 0) {
                res.status(400).json({
                    status: 400,
                    error: 'Validation Failed',
                    message: 'Invalid input data',
                    validationErrors,
                    timestamp: new Date().toISOString()
                });
                return;
            }

            console.log(`PUT /api/products/${id} - Updating product`);
            const product = await this.productService.updateProduct(id, request);
            res.json(product);
        } catch (error) {
            console.error('Error updating product:', error);
            if (error instanceof Error && error.message.includes('not found')) {
                res.status(404).json({
                    status: 404,
                    error: 'Product Not Found',
                    message: error.message,
                    timestamp: new Date().toISOString()
                });
            } else {
                res.status(500).json({
                    status: 500,
                    error: 'Internal Server Error',
                    message: 'Failed to update product',
                    timestamp: new Date().toISOString()
                });
            }
        }
    };

    deleteProduct = async (req: Request, res: Response): Promise<void> => {
        try {
            const id = parseInt(req.params.id);
            if (isNaN(id)) {
                res.status(400).json({
                    status: 400,
                    error: 'Bad Request',
                    message: 'Invalid product ID',
                    timestamp: new Date().toISOString()
                });
                return;
            }

            console.log(`DELETE /api/products/${id} - Deleting product`);
            await this.productService.deleteProduct(id);
            res.status(204).send();
        } catch (error) {
            console.error('Error deleting product:', error);
            if (error instanceof Error && error.message.includes('not found')) {
                res.status(404).json({
                    status: 404,
                    error: 'Product Not Found',
                    message: error.message,
                    timestamp: new Date().toISOString()
                });
            } else {
                res.status(500).json({
                    status: 500,
                    error: 'Internal Server Error',
                    message: 'Failed to delete product',
                    timestamp: new Date().toISOString()
                });
            }
        }
    };

    increaseStock = async (req: Request, res: Response): Promise<void> => {
        try {
            const id = parseInt(req.params.id);
            if (isNaN(id)) {
                res.status(400).json({
                    status: 400,
                    error: 'Bad Request',
                    message: 'Invalid product ID',
                    timestamp: new Date().toISOString()
                });
                return;
            }

            const request: StockUpdateRequest = req.body;

            // Validate request
            if (!request.quantity || request.quantity <= 0) {
                res.status(400).json({
                    status: 400,
                    error: 'Validation Failed',
                    message: 'Quantity must be greater than 0',
                    timestamp: new Date().toISOString()
                });
                return;
            }

            console.log(`POST /api/products/${id}/stock/increase - Increasing stock by ${request.quantity}`);
            const product = await this.productService.increaseStock(id, request);
            res.json(product);
        } catch (error) {
            console.error('Error increasing stock:', error);
            if (error instanceof Error && error.message.includes('not found')) {
                res.status(404).json({
                    status: 404,
                    error: 'Product Not Found',
                    message: error.message,
                    timestamp: new Date().toISOString()
                });
            } else {
                res.status(500).json({
                    status: 500,
                    error: 'Internal Server Error',
                    message: 'Failed to increase stock',
                    timestamp: new Date().toISOString()
                });
            }
        }
    };

    decreaseStock = async (req: Request, res: Response): Promise<void> => {
        try {
            const id = parseInt(req.params.id);
            if (isNaN(id)) {
                res.status(400).json({
                    status: 400,
                    error: 'Bad Request',
                    message: 'Invalid product ID',
                    timestamp: new Date().toISOString()
                });
                return;
            }

            const request: StockUpdateRequest = req.body;

            // Validate request
            if (!request.quantity || request.quantity <= 0) {
                res.status(400).json({
                    status: 400,
                    error: 'Validation Failed',
                    message: 'Quantity must be greater than 0',
                    timestamp: new Date().toISOString()
                });
                return;
            }

            console.log(`POST /api/products/${id}/stock/decrease - Decreasing stock by ${request.quantity}`);
            const product = await this.productService.decreaseStock(id, request);
            res.json(product);
        } catch (error) {
            console.error('Error decreasing stock:', error);
            if (error instanceof Error && error.message.includes('not found')) {
                res.status(404).json({
                    status: 404,
                    error: 'Product Not Found',
                    message: error.message,
                    timestamp: new Date().toISOString()
                });
            } else if (error instanceof Error && error.message.includes('Insufficient stock')) {
                res.status(400).json({
                    status: 400,
                    error: 'Insufficient Stock',
                    message: error.message,
                    timestamp: new Date().toISOString()
                });
            } else {
                res.status(500).json({
                    status: 500,
                    error: 'Internal Server Error',
                    message: 'Failed to decrease stock',
                    timestamp: new Date().toISOString()
                });
            }
        }
    };

    getLowStockProducts = async (req: Request, res: Response): Promise<void> => {
        try {
            console.log('GET /api/products/low-stock - Fetching low stock products');
            const products = await this.productService.getLowStockProducts();
            res.json(products);
        } catch (error) {
            console.error('Error fetching low stock products:', error);
            res.status(500).json({
                status: 500,
                error: 'Internal Server Error',
                message: 'Failed to fetch low stock products',
                timestamp: new Date().toISOString()
            });
        }
    };
}