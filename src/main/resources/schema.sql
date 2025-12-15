-- schema.sql: create tables expected by JdbcUserDetailsManager
-- Run once automatically by Spring Boot on startup (if spring.datasource.initialization-mode is enabled or default behavior depending on Spring Boot version)

CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(50) NOT NULL PRIMARY KEY,
  password VARCHAR(500) NOT NULL,
  enabled BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS authorities (
  username VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL,
  CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users(username)
);

CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username ON authorities (username, authority);

-- Update addresses table with new fields
ALTER TABLE IF EXISTS addresses ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20);
ALTER TABLE IF EXISTS addresses ADD COLUMN IF NOT EXISTS recipient_name VARCHAR(50);
ALTER TABLE IF EXISTS addresses ADD COLUMN IF NOT EXISTS address_type VARCHAR(20) DEFAULT 'SHIPPING';
ALTER TABLE IF EXISTS addresses ADD COLUMN IF NOT EXISTS is_default BOOLEAN DEFAULT false;

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
  order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  address_id BIGINT NOT NULL,
  order_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  total_amount DECIMAL(10, 2) DEFAULT 0.0,
  discount_amount DECIMAL(10, 2) DEFAULT 0.0,
  final_amount DECIMAL(10, 2) DEFAULT 0.0,
  order_date DATETIME NOT NULL,
  estimated_delivery_date DATETIME,
  delivery_date DATETIME,
  order_notes TEXT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (user_id) REFERENCES app_users(user_id),
  FOREIGN KEY (address_id) REFERENCES addresses(address_id),
  INDEX idx_user_id (user_id),
  INDEX idx_order_status (order_status),
  INDEX idx_order_date (order_date)
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
  order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  price_per_unit DECIMAL(10, 2) NOT NULL,
  discount DECIMAL(10, 2) DEFAULT 0.0,
  item_total DECIMAL(10, 2) NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(product_id),
  INDEX idx_order_id (order_id),
  INDEX idx_product_id (product_id)
);

-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
  payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  payment_method VARCHAR(50),
  transaction_id VARCHAR(100),
  payment_gateway VARCHAR(50),
  payment_reference VARCHAR(255),
  payment_date DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  notes TEXT,
  FOREIGN KEY (order_id) REFERENCES orders(order_id),
  FOREIGN KEY (user_id) REFERENCES app_users(user_id),
  INDEX idx_user_id (user_id),
  INDEX idx_order_id (order_id),
  INDEX idx_payment_status (payment_status),
  UNIQUE KEY unique_transaction_id (transaction_id)
);

-- Create user_addresses junction table if not exists
CREATE TABLE IF NOT EXISTS user_addresses (
  user_id BIGINT NOT NULL,
  address_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, address_id),
  FOREIGN KEY (user_id) REFERENCES app_users(user_id),
  FOREIGN KEY (address_id) REFERENCES addresses(address_id)
);
