import 'reflect-metadata';
import { createApp } from './app';

const PORT = process.env.PORT || 8080;

const app = createApp();

app.listen(PORT, () => {
    console.log(`🚀 Inventory Management API is running on port ${PORT}`);
    console.log(`📊 Health check: http://localhost:${PORT}/health`);
    console.log(`📦 Products API: http://localhost:${PORT}/api/products`);
});