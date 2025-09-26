import { Router } from 'express';
import { ProductController } from '../controllers/ProductController';

export function createProductRoutes(productController: ProductController): Router {
    const router = Router();

    // Get all products
    router.get('/', productController.getAllProducts);

    // Get low stock products (must be before /:id route)
    router.get('/low-stock', productController.getLowStockProducts);

    // Get product by ID
    router.get('/:id', productController.getProductById);

    // Create new product
    router.post('/', productController.createProduct);

    // Update product
    router.put('/:id', productController.updateProduct);

    // Delete product
    router.delete('/:id', productController.deleteProduct);

    // Stock operations
    router.post('/:id/stock/increase', productController.increaseStock);
    router.post('/:id/stock/decrease', productController.decreaseStock);

    return router;
}