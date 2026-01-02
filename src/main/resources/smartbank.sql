-- Create database
CREATE DATABASE IF NOT EXISTS smartbank;
USE smartbank;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    address TEXT,
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table
CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    account_type ENUM('SAVINGS', 'CURRENT') DEFAULT 'SAVINGS',
    status ENUM('PENDING', 'ACTIVE', 'SUSPENDED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Transactions table
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    from_account VARCHAR(20),
    to_account VARCHAR(20),
    amount DECIMAL(15,2) NOT NULL,
    type ENUM('TRANSFER', 'DEPOSIT', 'WITHDRAWAL') DEFAULT 'TRANSFER',
    description VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_account) REFERENCES accounts(account_number),
    FOREIGN KEY (to_account) REFERENCES accounts(account_number)
);

-- Admins table
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role ENUM('ADMIN', 'CLERK') DEFAULT 'CLERK',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default admin accounts
INSERT INTO admins (username, password_hash, email, role) VALUES 
('admin', '$2a$10$8K1p/a0dRL1SzYf3gkCk4u3U7K1lF9bGfJ5Xa6c3yY9bN1VcXrOqW', 'admin@smartbank.com', 'ADMIN'),
('clerk', '$2a$10$8K1p/a0dRL1SzYf3gkCk4u3U7K1lF9bGfJ5Xa6c3yY9bN1VcXrOqW', 'clerk@smartbank.com', 'CLERK');

-- Create some sample users and accounts for testing
INSERT INTO users (first_name, last_name, email, phone, address, date_of_birth) VALUES 
('John', 'Doe', 'john.doe@email.com', '1234567890', '123 Main St, City', '1990-01-15'),
('Jane', 'Smith', 'jane.smith@email.com', '0987654321', '456 Oak Ave, Town', '1985-05-20');

INSERT INTO accounts (account_number, user_id, balance, account_type, status) VALUES 
('100000001', 1, 5000.00, 'SAVINGS', 'ACTIVE'),
('100000002', 2, 3000.00, 'SAVINGS', 'ACTIVE');