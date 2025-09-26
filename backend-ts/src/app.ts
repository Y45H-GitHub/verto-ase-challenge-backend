import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import { Database } from './database/Database';
import { ProductService } from './services/ProductService';
import { ProductController } from './controllers/ProductController';
import { createProductRoutes } from './routes/productRoutes';

export function createApp(database?: Database): express.Application {
    const app = express();

    // Security middleware
    app.use(helmet());

    // CORS middleware
    app.use(cors());

    // Logging middleware
    app.use(morgan('combined'));

    // Body parsing middleware
    app.use(express.json());
    app.use(express.urlencoded({ extended: true }));

    // Initialize database and services
    const db = database || new Database();
    const productService = new ProductService(db);
    const productController = new ProductController(productService);

    // Routes
    app.use('/api/products', createProductRoutes(productController));

    // Health check endpoint
    app.get('/health', (req, res) => {
        res.json({
            status: 'OK',
            timestamp: new Date().toISOString(),
            service: 'Inventory Management API',
            version: '1.0.0'
        });
    });

    // Root endpoint
    app.get('/', (req, res) => {
        res.json({
            message: 'Inventory Management API',
            version: '1.0.0',
            endpoints: {
                products: '/api/products',
                health: '/health'
            }
        });
    });

    // 404 handler
    app.use('*', (req, res) => {
        res.status(404).json({
            status: 404,
            error: 'Not Found',
            message: `Route ${req.originalUrl} not found`,
            timestamp: new Date().toISOString()
        });
    });

    // Global error handler
    app.use((err: Error, req: express.Request, res: express.Response, next: express.NextFunction) => {
        console.error('Unhandled error:', err);
        res.status(500).json({
            status: 500,
            error: 'Internal Server Error',
            message: 'An unexpected error occurred',
            timestamp: new Date().toISOString()
        });
    });

    return app;
}