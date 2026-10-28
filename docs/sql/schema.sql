-- ============================================================
-- AutoParts Portal - Database Schema
-- PostgreSQL DDL
-- ============================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(20) UNIQUE NOT NULL
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(150) UNIQUE NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    enabled     BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User-Roles mapping
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    description     TEXT NOT NULL,
    sku             VARCHAR(30) UNIQUE NOT NULL,
    price           DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    stock_quantity  INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    category        VARCHAR(100) NOT NULL,
    brand           VARCHAR(100),
    image_url       VARCHAR(500),
    active          BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id),
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount     DECIMAL(10, 2) NOT NULL,
    shipping_address TEXT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP
);

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id  BIGINT NOT NULL REFERENCES products(id),
    quantity    INTEGER NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(10, 2) NOT NULL,
    subtotal    DECIMAL(10, 2) NOT NULL
);

-- Cart items table
CREATE TABLE IF NOT EXISTS cart_items (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id  BIGINT NOT NULL REFERENCES products(id),
    quantity    INTEGER NOT NULL CHECK (quantity > 0),
    UNIQUE (user_id, product_id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_sku ON products(sku);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_user_id ON cart_items(user_id);

-- Seed sample products
INSERT INTO products (name, description, sku, price, stock_quantity, category, brand, active)
VALUES
  ('Bosch Spark Plug Iridium', 'High-performance iridium spark plug for petrol engines, long life 100k km', 'BSP-001', 299.99, 50, 'Engine', 'Bosch', true),
  ('Denso Air Filter', 'Premium air filter for engine protection and improved airflow', 'DAF-001', 450.00, 35, 'Filters', 'Denso', true),
  ('MRF Tyre 185/65R15', 'Passenger radial tyre, excellent wet and dry grip', 'MRF-001', 3500.00, 20, 'Tyres', 'MRF', true),
  ('Valeo Brake Pad Set', 'Ceramic brake pads for front axle, low dust and noise', 'VBP-001', 1200.00, 15, 'Brakes', 'Valeo', true),
  ('ACDelco Engine Oil 5W30', 'Fully synthetic engine oil, 1 litre, API SN rated', 'ACO-001', 350.00, 100, 'Lubricants', 'ACDelco', true),
  ('NGK O2 Sensor', 'Wideband oxygen sensor for fuel injection system', 'NGK-001', 1850.00, 12, 'Engine', 'NGK', true),
  ('Exide Battery 55Ah', '55Ah maintenance-free car battery, 18-month warranty', 'EXB-001', 4200.00, 8, 'Electrical', 'Exide', true),
  ('Minda Headlight Bulb H4', 'H4 halogen headlight bulb 60/55W for most Indian cars', 'MHB-001', 180.00, 75, 'Electrical', 'Minda', true),
  ('Gabriel Shock Absorber', 'Front hydraulic shock absorber for Maruti Suzuki Swift', 'GSA-001', 1650.00, 18, 'Suspension', 'Gabriel', true),
  ('Schaeffler Timing Belt Kit', 'Complete timing belt kit with tensioner and idler pulley', 'STB-001', 2800.00, 10, 'Engine', 'Schaeffler', true),
  ('Mahle Oil Filter', 'Premium oil filter for extended oil change intervals', 'MOF-001', 280.00, 60, 'Filters', 'Mahle', true),
  ('Bosch Wiper Blade 21"', 'Aerotwin flat wiper blade 21 inch, streak-free performance', 'BWB-001', 420.00, 40, 'Exterior', 'Bosch', true)
ON CONFLICT (sku) DO NOTHING;
