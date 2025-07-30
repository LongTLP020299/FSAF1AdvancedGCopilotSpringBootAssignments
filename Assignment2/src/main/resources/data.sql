-- Sample data for testing the e-commerce review system
-- This file can be used to manually insert test data for MySQL

-- Insert sample users (avoid duplicates)
INSERT IGNORE INTO users (username, email, password, first_name, last_name, role, created_at) VALUES
('admin', 'admin@example.com', '$2a$10$DowJonesIndex', 'Admin', 'User', 'ADMIN', NOW()),
('john_doe', 'john@example.com', '$2a$10$DowJonesIndex', 'John', 'Doe', 'USER', NOW()),
('jane_smith', 'jane@example.com', '$2a$10$DowJonesIndex', 'Jane', 'Smith', 'USER', NOW());

-- Insert sample products (avoid duplicates)
INSERT IGNORE INTO products (name, description, price, stock_quantity, category, average_rating, created_at) VALUES
('iPhone 15 Pro', 'Latest Apple smartphone with advanced features', 999.99, 50, 'Electronics', 0.0, NOW()),
('Samsung Galaxy S24', 'High-performance Android smartphone', 899.99, 30, 'Electronics', 0.0, NOW()),
('MacBook Pro M3', 'Professional laptop for developers', 1999.99, 20, 'Computers', 0.0, NOW()),
('Wireless Headphones', 'Noise-cancelling Bluetooth headphones', 199.99, 100, 'Audio', 0.0, NOW());

-- Insert sample orders using subqueries to get user IDs (avoid duplicates)
INSERT IGNORE INTO orders (user_id, total_amount, status, order_date, shipping_address) 
SELECT u.id, 999.99, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 30 DAY), '123 Main St, City, State'
FROM users u WHERE u.username = 'john_doe'
UNION ALL
SELECT u.id, 1099.98, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 15 DAY), '456 Oak Ave, City, State'
FROM users u WHERE u.username = 'jane_smith'
UNION ALL
SELECT u.id, 199.99, 'SHIPPED', DATE_SUB(NOW(), INTERVAL 5 DAY), '123 Main St, City, State'
FROM users u WHERE u.username = 'john_doe';

-- Insert sample order items using subqueries (avoid duplicates)
INSERT IGNORE INTO order_items (order_id, product_id, quantity, price) 
SELECT o.id, p.id, 1, 999.99
FROM orders o 
JOIN users u ON o.user_id = u.id 
JOIN products p ON p.name = 'iPhone 15 Pro'
WHERE u.username = 'john_doe' AND o.total_amount = 999.99
UNION ALL
SELECT o.id, p.id, 1, 899.99
FROM orders o 
JOIN users u ON o.user_id = u.id 
JOIN products p ON p.name = 'Samsung Galaxy S24'
WHERE u.username = 'jane_smith' AND o.total_amount = 1099.98
UNION ALL
SELECT o.id, p.id, 1, 199.99
FROM orders o 
JOIN users u ON o.user_id = u.id 
JOIN products p ON p.name = 'Wireless Headphones'
WHERE u.username = 'jane_smith' AND o.total_amount = 1099.98
UNION ALL
SELECT o.id, p.id, 1, 199.99
FROM orders o 
JOIN users u ON o.user_id = u.id 
JOIN products p ON p.name = 'Wireless Headphones'
WHERE u.username = 'john_doe' AND o.total_amount = 199.99;
