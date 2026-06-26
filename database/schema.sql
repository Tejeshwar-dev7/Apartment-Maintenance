DROP DATABASE IF EXISTS apartment_maintenance;
CREATE DATABASE apartment_maintenance;
USE apartment_maintenance;

CREATE TABLE flats (
    id INT PRIMARY KEY AUTO_INCREMENT,
    flat_number VARCHAR(20) NOT NULL UNIQUE,
    tower VARCHAR(50) NOT NULL,
    floor_number INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role ENUM('RESIDENT', 'STAFF', 'ADMIN') NOT NULL,
    flat_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_flat FOREIGN KEY (flat_id) REFERENCES flats(id)
);

CREATE TABLE complaints (
    id INT PRIMARY KEY AUTO_INCREMENT,
    resident_id INT NOT NULL,
    assigned_staff_id INT NULL,
    title VARCHAR(140) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'OPEN',
    photo_url VARCHAR(255) NULL,
    completion_photo_url VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    CONSTRAINT fk_complaints_resident FOREIGN KEY (resident_id) REFERENCES users(id),
    CONSTRAINT fk_complaints_staff FOREIGN KEY (assigned_staff_id) REFERENCES users(id)
);

CREATE TABLE visitor_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    resident_id INT NOT NULL,
    visitor_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    purpose VARCHAR(160) NOT NULL,
    visit_time DATETIME NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    decision_note VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_visitors_resident FOREIGN KEY (resident_id) REFERENCES users(id)
);

CREATE TABLE maintenance_payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    flat_id INT NOT NULL,
    due_month CHAR(7) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(160) NOT NULL DEFAULT 'Monthly maintenance',
    status ENUM('DUE', 'PAID') NOT NULL DEFAULT 'DUE',
    transaction_id VARCHAR(40) NULL,
    paid_on DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_payment_flat_month (flat_id, due_month),
    CONSTRAINT fk_payments_flat FOREIGN KEY (flat_id) REFERENCES flats(id)
);

CREATE TABLE announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL,
    message TEXT NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_announcements_user FOREIGN KEY (created_by) REFERENCES users(id)
);

INSERT INTO flats (flat_number, tower, floor_number) VALUES
('A-101', 'A', 1),
('A-102', 'A', 1),
('B-201', 'B', 2);

INSERT INTO users (name, email, password_hash, role, flat_id) VALUES
('System Admin', 'admin@ams.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN', NULL),
('Riya Sharma', 'resident@ams.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'RESIDENT', 1),
('Arun Technician', 'staff@ams.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STAFF', NULL),
('Kiran Electrician', 'kiran.staff@ams.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STAFF', NULL);

INSERT INTO complaints (resident_id, assigned_staff_id, title, category, description, status) VALUES
(2, 3, 'Low water pressure', 'Water', 'Water pressure is very low in A-101 kitchen line.', 'ASSIGNED'),
(2, NULL, 'Lift display not working', 'Lift', 'Ground floor lift display is blank.', 'OPEN');

INSERT INTO visitor_requests (resident_id, visitor_name, phone, purpose, visit_time, status) VALUES
(2, 'Meera Kapoor', '9876543210', 'Family visit', '2026-06-28 18:30:00', 'PENDING');

INSERT INTO maintenance_payments (flat_id, due_month, amount, description, status) VALUES
(1, '2026-06', 3500.00, 'Monthly maintenance', 'DUE'),
(2, '2026-06', 3500.00, 'Monthly maintenance', 'DUE'),
(3, '2026-06', 4200.00, 'Repair charges', 'DUE');

INSERT INTO announcements (title, message, created_by) VALUES
('Water tank cleaning on Sunday', 'Water supply may be interrupted between 10 AM and 12 PM during tank cleaning.', 1),
('Visitor gate update', 'All visitor entries will require resident approval before security permits entry.', 1);
