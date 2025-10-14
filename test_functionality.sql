-- NovaBook Library Management System - Sample Data for Testing
-- Execute this script after creating the database schema to populate with test data

-- Sample Books
INSERT INTO Libro (titulo, autor, isbn, anio_publicacion, disponible) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 1925, TRUE),
('To Kill a Mockingbird', 'Harper Lee', '978-0-06-112008-4', 1960, TRUE),
('1984', 'George Orwell', '978-0-452-28423-4', 1949, TRUE),
('Pride and Prejudice', 'Jane Austen', '978-0-14-143951-8', 1813, TRUE),
('The Catcher in the Rye', 'J.D. Salinger', '978-0-316-76948-0', 1951, TRUE),
('Lord of the Flies', 'William Golding', '978-0-571-05686-2', 1954, TRUE),
('Animal Farm', 'George Orwell', '978-0-452-28424-1', 1945, TRUE),
('Brave New World', 'Aldous Huxley', '978-0-06-085052-4', 1932, TRUE);

-- Sample Partners/Members
INSERT INTO Socio (nombre, direccion, telefono, email, activo) VALUES
('John Smith', '123 Main St, Anytown USA', '555-0101', 'john.smith@email.com', TRUE),
('Maria Garcia', '456 Oak Ave, Springfield', '555-0102', 'maria.garcia@email.com', TRUE),
('David Johnson', '789 Pine Rd, Riverside', '555-0103', 'david.johnson@email.com', TRUE),
('Sarah Wilson', '321 Elm St, Lakewood', '555-0104', 'sarah.wilson@email.com', TRUE),
('Michael Brown', '654 Maple Dr, Hillside', '555-0105', 'michael.brown@email.com', TRUE),
('Emily Davis', '987 Cedar Ln, Oceanview', '555-0106', 'emily.davis@email.com', FALSE),
('James Miller', '147 Birch Way, Mountain View', '555-0107', 'james.miller@email.com', TRUE),
('Lisa Anderson', '258 Ash Ct, Valley View', '555-0108', 'lisa.anderson@email.com', TRUE);

-- Sample Users
INSERT INTO Usuario (nombre, email, password, activo) VALUES
('Administrator', 'admin@novabook.com', SHA2('admin123', 256), TRUE),
('Librarian', 'librarian@novabook.com', SHA2('lib123', 256), TRUE),
('Assistant', 'assistant@novabook.com', SHA2('asst123', 256), TRUE);

-- Sample Roles
INSERT INTO Rol (nombre) VALUES
('Administrator'),
('Librarian'),
('Assistant'),
('Read Only');

-- Assign roles to users
INSERT INTO Usuario_Rol (usuario_id, rol_id) VALUES
(1, 1), -- Administrator has Administrator role
(2, 2), -- Librarian has Librarian role
(3, 3), -- Assistant has Assistant role
(3, 4); -- Assistant also has Read Only role

-- Sample Active Loans (for testing)
INSERT INTO Prestamo (libro_id, socio_id, fecha_prestamo, devuelto) VALUES
(1, 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), FALSE),  -- 5 days ago, still active
(3, 2, DATE_SUB(CURDATE(), INTERVAL 10 DAY), FALSE), -- 10 days ago, still active
(5, 4, DATE_SUB(CURDATE(), INTERVAL 20 DAY), FALSE); -- 20 days ago, OVERDUE!

-- Mark books as unavailable that are on loan
UPDATE Libro SET disponible = FALSE WHERE id IN (1, 3, 5);

-- Sample Returned Loans (for history)
INSERT INTO Prestamo (libro_id, socio_id, fecha_prestamo, fecha_devolucion, devuelto) VALUES
(2, 3, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 20 DAY), TRUE),
(4, 1, DATE_SUB(CURDATE(), INTERVAL 25 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY), TRUE),
(6, 5, DATE_SUB(CURDATE(), INTERVAL 35 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY), TRUE);

-- Display summary
SELECT 
    'Database populated successfully!' as Status,
    (SELECT COUNT(*) FROM Libro) as Total_Books,
    (SELECT COUNT(*) FROM Libro WHERE disponible = TRUE) as Available_Books,
    (SELECT COUNT(*) FROM Socio) as Total_Partners,
    (SELECT COUNT(*) FROM Socio WHERE activo = TRUE) as Active_Partners,
    (SELECT COUNT(*) FROM Prestamo) as Total_Loans,
    (SELECT COUNT(*) FROM Prestamo WHERE devuelto = FALSE) as Active_Loans,
    (SELECT COUNT(*) FROM Usuario) as Total_Users;