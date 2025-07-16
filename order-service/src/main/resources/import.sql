INSERT INTO orders (id, user_id, username, status, created_at, updated_at, total_amount) VALUES (1, 1, 'admin', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1350.00);
INSERT INTO orders (id, user_id, username, status, created_at, updated_at, total_amount) VALUES (2, 2, 'user', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 812.99);

INSERT INTO order_item (id, order_id, product_id, product_name, quantity, unit_price, total_price) VALUES (1, 1, 1, 'Laptop', 1, 1200.00, 1200.00);
INSERT INTO order_item (id, order_id, product_id, product_name, quantity, unit_price, total_price) VALUES (2, 1, 3, 'Desk Chair', 1, 150.00, 150.00);
INSERT INTO order_item (id, order_id, product_id, product_name, quantity, unit_price, total_price) VALUES (3, 2, 2, 'Smartphone', 1, 800.00, 800.00);
INSERT INTO order_item (id, order_id, product_id, product_name, quantity, unit_price, total_price) VALUES (4, 2, 4, 'Coffee Mug', 1, 12.99, 12.99); 