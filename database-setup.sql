-- ============================================
-- DATABASE SETUP SCRIPT
-- Sales Transaction Consumer Application
-- ============================================

-- Create database (run as superuser)
CREATE DATABASE sales_db;

-- Connect to the database
\c sales_db;

-- ============================================
-- CREATE TABLES
-- ============================================

-- Category table
CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Supplier table
CREATE TABLE IF NOT EXISTS supplier (
    id SERIAL PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Product table
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    sku VARCHAR(50) UNIQUE NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    category_id INTEGER REFERENCES category(id),
    supplier_id INTEGER REFERENCES supplier(id),
    current_stock INTEGER DEFAULT 0,
    price DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transaction history table
CREATE TABLE IF NOT EXISTS transaction_history (
    id SERIAL PRIMARY KEY,
    transaction_date DATE NOT NULL,
    total_price DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transaction detail table
CREATE TABLE IF NOT EXISTS transaction_detail (
    id SERIAL PRIMARY KEY,
    transaction_id INTEGER REFERENCES transaction_history(id) ON DELETE CASCADE,
    product_id INTEGER REFERENCES product(id),
    qty INTEGER NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    total_price DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stock log table
CREATE TABLE IF NOT EXISTS stock_log (
    id SERIAL PRIMARY KEY,
    product_id INTEGER REFERENCES product(id),
    quantity_change INTEGER NOT NULL,
    log_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- CREATE INDEXES FOR PERFORMANCE
-- ============================================

CREATE INDEX idx_product_sku ON product(sku);
CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_supplier ON product(supplier_id);
CREATE INDEX idx_transaction_date ON transaction_history(transaction_date);
CREATE INDEX idx_transaction_detail_transaction ON transaction_detail(transaction_id);
CREATE INDEX idx_transaction_detail_product ON transaction_detail(product_id);
CREATE INDEX idx_stock_log_product ON stock_log(product_id);
CREATE INDEX idx_stock_log_date ON stock_log(created_at);

-- ============================================
-- INSERT SAMPLE DATA
-- ============================================

-- Sample categories
INSERT INTO category (category_name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Office Supplies', 'Office equipment and supplies'),
('Furniture', 'Office and home furniture');

-- Sample suppliers
INSERT INTO supplier (supplier_name, contact_person, phone, email) VALUES
('Tech Supplier Inc', 'John Doe', '+1-555-0100', 'john@techsupplier.com'),
('Office Warehouse', 'Jane Smith', '+1-555-0200', 'jane@officewarehouse.com'),
('Furniture Plus', 'Bob Johnson', '+1-555-0300', 'bob@furnitureplus.com');

-- Sample products
INSERT INTO product (sku, product_name, category_id, supplier_id, current_stock, price) VALUES
('LAP001', 'Laptop ASUS ROG', 1, 1, 50, 8500000.00),
('MOU001', 'Wireless Mouse Logitech', 1, 1, 200, 150000.00),
('KEY001', 'Mechanical Keyboard RGB', 1, 1, 100, 750000.00),
('MON001', 'Monitor 27" 4K', 1, 1, 30, 3500000.00),
('PEN001', 'Ballpoint Pen Blue (Box of 12)', 2, 2, 500, 25000.00),
('PAP001', 'A4 Paper (Ream)', 2, 2, 300, 45000.00),
('CHA001', 'Office Chair Ergonomic', 3, 3, 50, 1200000.00),
('DES001', 'Office Desk Wood 120cm', 3, 3, 25, 2500000.00);

-- ============================================
-- CREATE VIEWS FOR REPORTING
-- ============================================

-- Daily sales summary
CREATE OR REPLACE VIEW v_daily_sales AS
SELECT 
    th.transaction_date,
    COUNT(DISTINCT th.id) as total_transactions,
    SUM(td.qty) as total_items_sold,
    SUM(th.total_price) as total_revenue
FROM transaction_history th
LEFT JOIN transaction_detail td ON th.id = td.transaction_id
GROUP BY th.transaction_date
ORDER BY th.transaction_date DESC;

-- Product stock status
CREATE OR REPLACE VIEW v_product_stock AS
SELECT 
    p.id,
    p.sku,
    p.product_name,
    c.category_name,
    s.supplier_name,
    p.current_stock,
    p.price,
    (p.current_stock * p.price) as stock_value,
    CASE 
        WHEN p.current_stock = 0 THEN 'OUT OF STOCK'
        WHEN p.current_stock <= 10 THEN 'LOW STOCK'
        WHEN p.current_stock <= 50 THEN 'MEDIUM STOCK'
        ELSE 'GOOD STOCK'
    END as stock_status
FROM product p
LEFT JOIN category c ON p.category_id = c.id
LEFT JOIN supplier s ON p.supplier_id = s.id
ORDER BY p.current_stock ASC;

-- Stock movement history
CREATE OR REPLACE VIEW v_stock_movement AS
SELECT 
    sl.id,
    sl.created_at,
    p.sku,
    p.product_name,
    sl.quantity_change,
    sl.log_type,
    LAG(p.current_stock) OVER (PARTITION BY p.id ORDER BY sl.created_at) + sl.quantity_change as stock_before,
    p.current_stock as current_stock
FROM stock_log sl
JOIN product p ON sl.product_id = p.id
ORDER BY sl.created_at DESC;

-- ============================================
-- VERIFY INSTALLATION
-- ============================================

-- Show all tables
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public'
ORDER BY table_name;

-- Show sample data
SELECT 'Categories' as table_name, COUNT(*) as row_count FROM category
UNION ALL
SELECT 'Suppliers', COUNT(*) FROM supplier
UNION ALL
SELECT 'Products', COUNT(*) FROM product;

-- ============================================
-- GRANT PERMISSIONS (Optional)
-- ============================================

-- If using a specific user, grant necessary permissions:
-- GRANT ALL PRIVILEGES ON DATABASE sales_db TO your_user;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO your_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO your_user;

-- ============================================
-- CLEANUP SCRIPT (Use with caution!)
-- ============================================

-- Uncomment below to drop all tables (WARNING: This will delete all data!)
-- DROP VIEW IF EXISTS v_stock_movement;
-- DROP VIEW IF EXISTS v_product_stock;
-- DROP VIEW IF EXISTS v_daily_sales;
-- DROP TABLE IF EXISTS stock_log CASCADE;
-- DROP TABLE IF EXISTS transaction_detail CASCADE;
-- DROP TABLE IF EXISTS transaction_history CASCADE;
-- DROP TABLE IF EXISTS product CASCADE;
-- DROP TABLE IF EXISTS supplier CASCADE;
-- DROP TABLE IF EXISTS category CASCADE;
