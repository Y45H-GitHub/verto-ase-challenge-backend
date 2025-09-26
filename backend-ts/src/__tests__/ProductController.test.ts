import request from 'supertest';
import { createApp } from '../app';
import { Database } from '../database/Database';

describe('ProductController', () => {
    let app: any;
    let database: Database;

    beforeEach(async () => {
        database = new Database(':memory:');
        await database.waitForInitialization();
        app = createApp(database);
    });

    afterEach(() => {
        database.close();
    });

    describe('GET /api/products', () => {
        it('should return all products', async () => {
            const response = await request(app)
                .get('/api/products')
                .expect(200);

            expect(Array.isArray(response.body)).toBe(true);
            expect(response.body.length).toBeGreaterThan(0);
            expect(response.body[0]).toHaveProperty('id');
            expect(response.body[0]).toHaveProperty('name');
        });
    });

    describe('GET /api/products/:id', () => {
        it('should return a product by ID', async () => {
            const response = await request(app)
                .get('/api/products/1')
                .expect(200);

            expect(response.body).toHaveProperty('id', 1);
            expect(response.body).toHaveProperty('name');
        });

        it('should return 404 for non-existent product', async () => {
            const response = await request(app)
                .get('/api/products/999')
                .expect(404);

            expect(response.body).toHaveProperty('error', 'Product Not Found');
        });

        it('should return 400 for invalid ID', async () => {
            const response = await request(app)
                .get('/api/products/invalid')
                .expect(400);

            expect(response.body).toHaveProperty('error', 'Bad Request');
        });
    });

    describe('POST /api/products', () => {
        it('should create a new product', async () => {
            const productData = {
                name: 'Test Product',
                description: 'Test Description',
                stockQuantity: 100,
                lowStockThreshold: 10
            };

            const response = await request(app)
                .post('/api/products')
                .send(productData)
                .expect(201);

            expect(response.body).toHaveProperty('id');
            expect(response.body.name).toBe('Test Product');
            expect(response.body.stockQuantity).toBe(100);
        });

        it('should return 400 for invalid product data', async () => {
            const productData = {
                name: '',
                stockQuantity: -1
            };

            const response = await request(app)
                .post('/api/products')
                .send(productData)
                .expect(400);

            expect(response.body).toHaveProperty('error', 'Validation Failed');
            expect(response.body).toHaveProperty('validationErrors');
        });
    });

    describe('PUT /api/products/:id', () => {
        it('should update an existing product', async () => {
            const updateData = {
                name: 'Updated Product',
                stockQuantity: 150
            };

            const response = await request(app)
                .put('/api/products/1')
                .send(updateData)
                .expect(200);

            expect(response.body.name).toBe('Updated Product');
            expect(response.body.stockQuantity).toBe(150);
        });

        it('should return 404 for non-existent product', async () => {
            const updateData = { name: 'Updated Product' };

            const response = await request(app)
                .put('/api/products/999')
                .send(updateData)
                .expect(404);

            expect(response.body).toHaveProperty('error', 'Product Not Found');
        });
    });

    describe('DELETE /api/products/:id', () => {
        it('should delete an existing product', async () => {
            await request(app)
                .delete('/api/products/1')
                .expect(204);

            // Verify product is deleted
            await request(app)
                .get('/api/products/1')
                .expect(404);
        });

        it('should return 404 for non-existent product', async () => {
            const response = await request(app)
                .delete('/api/products/999')
                .expect(404);

            expect(response.body).toHaveProperty('error', 'Product Not Found');
        });
    });

    describe('POST /api/products/:id/stock/increase', () => {
        it('should increase stock quantity', async () => {
            const stockUpdate = { quantity: 50 };

            const response = await request(app)
                .post('/api/products/1/stock/increase')
                .send(stockUpdate)
                .expect(200);

            expect(response.body.stockQuantity).toBeGreaterThan(0);
        });

        it('should return 400 for invalid quantity', async () => {
            const stockUpdate = { quantity: 0 };

            const response = await request(app)
                .post('/api/products/1/stock/increase')
                .send(stockUpdate)
                .expect(400);

            expect(response.body).toHaveProperty('error', 'Validation Failed');
        });
    });

    describe('POST /api/products/:id/stock/decrease', () => {
        it('should decrease stock quantity', async () => {
            const stockUpdate = { quantity: 5 };

            const response = await request(app)
                .post('/api/products/1/stock/decrease')
                .send(stockUpdate)
                .expect(200);

            expect(response.body.stockQuantity).toBeGreaterThanOrEqual(0);
        });

        it('should return 400 for insufficient stock', async () => {
            const stockUpdate = { quantity: 1000 };

            const response = await request(app)
                .post('/api/products/1/stock/decrease')
                .send(stockUpdate)
                .expect(400);

            expect(response.body).toHaveProperty('error', 'Insufficient Stock');
        });
    });

    describe('GET /api/products/low-stock', () => {
        it('should return low stock products', async () => {
            const response = await request(app)
                .get('/api/products/low-stock')
                .expect(200);

            expect(Array.isArray(response.body)).toBe(true);

            // All returned products should have lowStock = true
            response.body.forEach((product: any) => {
                expect(product.lowStock).toBe(true);
            });
        });
    });

    describe('GET /health', () => {
        it('should return health status', async () => {
            const response = await request(app)
                .get('/health')
                .expect(200);

            expect(response.body).toHaveProperty('status', 'OK');
            expect(response.body).toHaveProperty('service', 'Inventory Management API');
        });
    });
});