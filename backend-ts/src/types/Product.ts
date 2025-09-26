export interface Product {
    id: number;
    name: string;
    description?: string;
    stockQuantity: number;
    lowStockThreshold: number;
    lowStock?: boolean;
}

export interface CreateProductRequest {
    name: string;
    description?: string;
    stockQuantity: number;
    lowStockThreshold?: number;
}

export interface UpdateProductRequest {
    name?: string;
    description?: string;
    stockQuantity?: number;
    lowStockThreshold?: number;
}

export interface StockUpdateRequest {
    quantity: number;
}