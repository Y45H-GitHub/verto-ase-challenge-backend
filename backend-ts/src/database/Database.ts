import sqlite3 from 'sqlite3';
import { Product } from '../types/Product';

export class Database {
    private db: sqlite3.Database;
    private initialized: Promise<void>;

    constructor(dbPath: string = ':memory:') {
        this.db = new sqlite3.Database(dbPath);
        this.initialized = this.initializeDatabase();
    }

    private async initializeDatabase(): Promise<void> {
        return new Promise((resolve, reject) => {
            const createTableQuery = `
      CREATE TABLE IF NOT EXISTS products (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        description TEXT,
        stock_quantity INTEGER NOT NULL CHECK (stock_quantity >= 0),
        low_stock_threshold INTEGER NOT NULL DEFAULT 10 CHECK (low_stock_threshold >= 0)
      )
    `;

            this.db.run(createTableQuery, (err) => {
                if (err) {
                    console.error('Error creating products table:', err);
                    reject(err);
                } else {
                    console.log('Products table created successfully');
                    this.insertSampleData().then(resolve).catch(reject);
                }
            });
        });
    }

    private async insertSampleData(): Promise<void> {
        const sampleProducts = [
            { name: 'Laptop', description: 'High-performance laptop', stockQuantity: 25, lowStockThreshold: 5 },
            { name: 'Mouse', description: 'Wireless optical mouse', stockQuantity: 150, lowStockThreshold: 20 },
            { name: 'Keyboard', description: 'Mechanical keyboard', stockQuantity: 75, lowStockThreshold: 10 },
            { name: 'Monitor', description: '24-inch LED monitor', stockQuantity: 8, lowStockThreshold: 15 },
            { name: 'Headphones', description: 'Noise-cancelling headphones', stockQuantity: 3, lowStockThreshold: 10 },
            { name: 'Webcam', description: 'HD webcam for video calls', stockQuantity: 45, lowStockThreshold: 8 }
        ];

        const insertQuery = `
      INSERT OR IGNORE INTO products (name, description, stock_quantity, low_stock_threshold)
      VALUES (?, ?, ?, ?)
    `;

        const insertPromises = sampleProducts.map(product => {
            return new Promise<void>((resolve, reject) => {
                this.db.run(insertQuery, [
                    product.name,
                    product.description,
                    product.stockQuantity,
                    product.lowStockThreshold
                ], (err) => {
                    if (err) reject(err);
                    else resolve();
                });
            });
        });

        await Promise.all(insertPromises);
    }

    public async waitForInitialization(): Promise<void> {
        return this.initialized;
    }

    public async getAllProducts(): Promise<Product[]> {
        await this.initialized;
        return new Promise((resolve, reject) => {
            const query = `
        SELECT 
          id,
          name,
          description,
          stock_quantity as stockQuantity,
          low_stock_threshold as lowStockThreshold,
          CASE WHEN stock_quantity <= low_stock_threshold THEN 1 ELSE 0 END as lowStock
        FROM products
        ORDER BY id
      `;

            this.db.all(query, (err, rows: any[]) => {
                if (err) {
                    reject(err);
                } else {
                    const products = rows.map(row => ({
                        ...row,
                        lowStock: Boolean(row.lowStock)
                    }));
                    resolve(products);
                }
            });
        });
    }

    public async getProductById(id: number): Promise<Product | null> {
        await this.initialized;
        return new Promise((resolve, reject) => {
            const query = `
        SELECT 
          id,
          name,
          description,
          stock_quantity as stockQuantity,
          low_stock_threshold as lowStockThreshold,
          CASE WHEN stock_quantity <= low_stock_threshold THEN 1 ELSE 0 END as lowStock
        FROM products
        WHERE id = ?
      `;

            this.db.get(query, [id], (err, row: any) => {
                if (err) {
                    reject(err);
                } else if (row) {
                    resolve({
                        ...row,
                        lowStock: Boolean(row.lowStock)
                    });
                } else {
                    resolve(null);
                }
            });
        });
    }

    public async createProduct(product: Omit<Product, 'id' | 'lowStock'>): Promise<Product> {
        await this.initialized;
        return new Promise((resolve, reject) => {
            const query = `
        INSERT INTO products (name, description, stock_quantity, low_stock_threshold)
        VALUES (?, ?, ?, ?)
      `;

            this.db.run(query, [
                product.name,
                product.description || null,
                product.stockQuantity,
                product.lowStockThreshold
            ], function (err) {
                if (err) {
                    reject(err);
                } else {
                    // Get the created product
                    resolve({
                        id: this.lastID,
                        ...product,
                        lowStock: product.stockQuantity <= product.lowStockThreshold
                    });
                }
            });
        });
    }

    public async updateProduct(id: number, updates: Partial<Omit<Product, 'id' | 'lowStock'>>): Promise<Product | null> {
        await this.initialized;
        // First check if product exists
        const existingProduct = await this.getProductById(id);
        if (!existingProduct) {
            return null;
        }

        const fields: string[] = [];
        const values: any[] = [];

        if (updates.name !== undefined) {
            fields.push('name = ?');
            values.push(updates.name);
        }
        if (updates.description !== undefined) {
            fields.push('description = ?');
            values.push(updates.description);
        }
        if (updates.stockQuantity !== undefined) {
            fields.push('stock_quantity = ?');
            values.push(updates.stockQuantity);
        }
        if (updates.lowStockThreshold !== undefined) {
            fields.push('low_stock_threshold = ?');
            values.push(updates.lowStockThreshold);
        }

        if (fields.length === 0) {
            return existingProduct;
        }

        values.push(id);
        const query = `UPDATE products SET ${fields.join(', ')} WHERE id = ?`;

        return new Promise((resolve, reject) => {
            this.db.run(query, values, (err) => {
                if (err) {
                    reject(err);
                } else {
                    // Return updated product
                    this.getProductById(id).then(resolve).catch(reject);
                }
            });
        });
    }

    public async deleteProduct(id: number): Promise<boolean> {
        await this.initialized;
        return new Promise((resolve, reject) => {
            const query = 'DELETE FROM products WHERE id = ?';

            this.db.run(query, [id], function (err) {
                if (err) {
                    reject(err);
                } else {
                    resolve(this.changes > 0);
                }
            });
        });
    }

    public async getLowStockProducts(): Promise<Product[]> {
        await this.initialized;
        return new Promise((resolve, reject) => {
            const query = `
        SELECT 
          id,
          name,
          description,
          stock_quantity as stockQuantity,
          low_stock_threshold as lowStockThreshold,
          1 as lowStock
        FROM products
        WHERE stock_quantity <= low_stock_threshold
        ORDER BY id
      `;

            this.db.all(query, (err, rows: any[]) => {
                if (err) {
                    reject(err);
                } else {
                    const products = rows.map(row => ({
                        ...row,
                        lowStock: true
                    }));
                    resolve(products);
                }
            });
        });
    }

    public close(): void {
        this.db.close();
    }
}