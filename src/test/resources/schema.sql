DROP ALL OBJECTS;

CREATE TABLE app_users
(
    user_id       IDENTITY PRIMARY KEY,
    name          VARCHAR(255)        NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    phone_number  VARCHAR(255),
    password_hash VARCHAR(255)        NOT NULL
);

CREATE TABLE user_roles
(
    user_id BIGINT       NOT NULL,
    role    VARCHAR(255) NOT NULL,

    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES app_users (user_id)
        ON DELETE CASCADE
);

CREATE TABLE categories
(
    category_id IDENTITY PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE products
(
    product_id     IDENTITY PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    discount_price DECIMAL(10, 2),
    price          DECIMAL(10, 2) NOT NULL,
    category_id    BIGINT         NOT NULL,
    created_at     TIMESTAMP DEFAULT NOW(),
    updated_at     TIMESTAMP DEFAULT NOW(),
    description    VARCHAR(1000),
    image_url      VARCHAR(1000),

    CONSTRAINT fk_category FOREIGN KEY (category_id)
        REFERENCES categories (category_id)
        ON DELETE RESTRICT
);

CREATE TABLE favorites
(
    favorite_id IDENTITY PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,

    CONSTRAINT uc_user_product UNIQUE (user_id, product_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES app_users (user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_product FOREIGN KEY (product_id)
        REFERENCES products (product_id)
        ON DELETE CASCADE
);

CREATE TABLE carts
(
    cart_id IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_cart_user FOREIGN KEY (user_id)
        REFERENCES app_users (user_id)
        ON DELETE CASCADE
);

CREATE TABLE cart_items
(
    cart_item_id IDENTITY PRIMARY KEY,
    cart_id      BIGINT NOT NULL,
    product_id   BIGINT NOT NULL,
    quantity     BIGINT NOT NULL,

    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id)
        REFERENCES carts (cart_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id)
        REFERENCES products (product_id)
        ON DELETE CASCADE,
    CONSTRAINT uc_cart_product UNIQUE (cart_id, product_id)
);

CREATE TABLE orders
(
    order_id         IDENTITY PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    delivery_address VARCHAR(255)   NOT NULL,
    contact_phone    VARCHAR(255),
    delivery_method  VARCHAR(255)   NOT NULL,
    status           VARCHAR(255)   NOT NULL CHECK (
        status IN ('CREATED', 'AWAITING_PAYMENT', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')
        ),
    created_at       TIMESTAMP DEFAULT NOW(),
    updated_at       TIMESTAMP DEFAULT NOW(),
    total_amount     DECIMAL(10, 2) NOT NULL,

    CONSTRAINT fk_order_user FOREIGN KEY (user_id)
        REFERENCES app_users (user_id)
        ON DELETE RESTRICT
);

CREATE TABLE order_items
(
    order_item_id     IDENTITY PRIMARY KEY,
    order_id          BIGINT         NOT NULL,
    product_id        BIGINT         NOT NULL,
    quantity          BIGINT         NOT NULL,
    price_at_purchase DECIMAL(10, 2) NOT NULL,

    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id)
        REFERENCES orders (order_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id)
        REFERENCES products (product_id)
        ON DELETE RESTRICT,
    CONSTRAINT uc_order_product UNIQUE (order_id, product_id)
);

CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_favorites_user_id ON favorites (user_id);
CREATE INDEX idx_favorites_product_id ON favorites (product_id);
CREATE INDEX idx_cart_items_cart_id ON cart_items (cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items (product_id);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
