INSERT INTO app_users (name, email, phone_number, password_hash)
VALUES ('Alice Johnson', 'alice.johnson@example.com', '+1234567890', '$2a$10$0HzYFEvYU7jsKwENj2bfN.ynV.tqvVm2hq38vvG0aeM.lCWVMFTWC'),
       ('Bob Smith', 'bob.smith@example.com', '+1987654321', '$2a$10$BzjVF9EnDrZFrjOhbJ/6.Otv4p7h3wy6VMqp2ztaoPKUA9MNjkDGW');

INSERT INTO user_roles(user_id, role)
VALUES (1, 'ROLE_USER'),
       (1, 'ROLE_ADMIN'),
       (2, 'ROLE_USER');

INSERT INTO categories (name)
VALUES ('Fertilizer'),
       ('Protective products and septic tanks'),
       ('Tools and equipment');

INSERT INTO products (name, discount_price, price, category_id, description, image_url, created_at, updated_at)
VALUES ('All-Purpose Plant Fertilizer', 8.99, 11.99, 1, 'Balanced NPK formula for all types of plants',
        'https://example.com/images/fertilizer_all_purpose.jpg', '2025-07-01 00:00:00', '2025-07-01 00:00:00'),
       ('Organic Tomato Feed', 9.49, 13.99, 1, 'Organic liquid fertilizer ideal for tomatoes and vegetables',
        'https://example.com/images/fertilizer_tomato_feed.jpg', '2025-07-01 00:00:00', '2025-07-01 00:00:00'),
       ('Slug & Snail Barrier Pellets', 5.75, 7.50, 2, 'Pet-safe barrier pellets to protect plants from slugs',
        'https://example.com/images/protection_slug_pellets.jpg', '2025-07-01 00:00:00', '2025-07-01 00:00:00');

INSERT INTO favorites(user_id, product_id)
VALUES (1, 1),
       (1, 2);

INSERT INTO carts (user_id)
VALUES (1),
       (2);

INSERT INTO cart_items (cart_id, product_id, quantity)
VALUES (1, 1, 2),
       (1, 2, 1),
       (2, 3, 1);

INSERT INTO orders (user_id, delivery_address, contact_phone, delivery_method, status, created_at, updated_at,
                    total_amount)
VALUES (1, '123 Garden Street', '+1234567890', 'COURIER', 'AWAITING_PAYMENT', '2025-07-01 10:00:00',
        '2025-07-01 10:30:00', 23.73),
       (2, '456 Green Ave', '+1987654321', 'PICKUP', 'CREATED', '2025-07-02 12:00:00', '2025-07-02 12:05:00', 5.75),
       (1, '123 Garden Street', '+1234567890', 'COURIER', 'DELIVERED', '2025-05-03T17:00:00', '2025-05-05T17:00:00',
        11.50),
       (2, '456 Green Ave', '+1987654321', 'PICKUP', 'CANCELLED', '2025-07-01T11:45:00', '2025-07-02T09:10:00', 8.99);

INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase)
VALUES (1, 1, 2, 8.99),
       (1, 3, 1, 5.75),
       (2, 3, 1, 5.75),
       (3, 3, 2, 5.75),
       (4, 1, 1, 8.99);
