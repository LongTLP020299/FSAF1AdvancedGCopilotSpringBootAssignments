-- Migration SQL để tạo full-text index cho MySQL
-- File: src/main/resources/db/migration/V3__Add_Fulltext_Index.sql

-- Tạo full-text index cho search performance
CREATE FULLTEXT INDEX idx_product_fulltext ON products(name, description);

-- Tạo thêm covering index cho các truy vấn phổ biến
CREATE INDEX idx_product_category_stock_price ON products(category, stock_quantity, price);

-- Index cho sorting theo rating
CREATE INDEX idx_product_rating_desc ON products(average_rating DESC, name ASC);

-- Index cho recent products
CREATE INDEX idx_product_created_desc ON products(created_at DESC);

-- Composite index cho filtered search với stock
CREATE INDEX idx_product_active_search ON products(stock_quantity, category, price, average_rating) 
WHERE stock_quantity > 0;
