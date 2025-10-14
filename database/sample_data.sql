-- ====================================================================
-- NovaBook Library Management System - Sample Data for Testing
-- Author: Luis Alfredo - Clan Cienaga
-- Date: October 2024
-- Description: Insert sample data for development and testing purposes
-- ====================================================================

USE Biblioteca;

-- ====== DATOS DE PRUEBA PARA DESARROLLO ======

-- Insertar usuarios adicionales para pruebas
INSERT IGNORE INTO Usuario (nombre, email, password, activo) VALUES 
('María González', 'maria@novabook.com', '$2a$10$E4X3.mPZj9R1iZeU3Zj9f.H5J6K7L8M9N0P1Q2R3S4T5U6V7W8X9Y0', TRUE),
('Carlos Rodríguez', 'carlos@novabook.com', '$2a$10$F5Y4.nQ0k0S2j1fV4Ak0g.I6K7L8M9N0P1Q2R3S4T5U6V7W8X9Y0Z1', TRUE),
('Ana Martínez', 'ana@novabook.com', '$2a$10$G6Z5.oR1l1T3k2gW5Bl1h.J7L8M9N0P1Q2R3S4T5U6V7W8X9Y0Z1A2', TRUE);

-- Asignar rol de bibliotecario a los usuarios de prueba
INSERT IGNORE INTO Usuario_Rol (usuario_id, rol_id) 
SELECT u.id, r.id 
FROM Usuario u, Rol r 
WHERE u.email IN ('maria@novabook.com', 'carlos@novabook.com', 'ana@novabook.com') 
  AND r.nombre = 'Bibliotecario';

-- Insertar socios de ejemplo
INSERT IGNORE INTO Socio (nombre, direccion, telefono, email, activo) VALUES 
('Juan Carlos Pérez', 'Calle 123 #45-67, Ciénaga, Magdalena', '301-555-0101', 'juan.perez@email.com', TRUE),
('Sofia Isabella López', 'Carrera 89 #12-34, Santa Marta, Magdalena', '320-555-0102', 'sofia.lopez@email.com', TRUE),
('Miguel Ángel Herrera', 'Avenida Libertador #56-78, Barranquilla, Atlántico', '315-555-0103', 'miguel.herrera@email.com', TRUE),
('Laura Valentina García', 'Calle Real #23-45, Cartagena, Bolívar', '310-555-0104', 'laura.garcia@email.com', TRUE),
('Diego Alejandro Moreno', 'Transversal 34 #67-89, Valledupar, Cesar', '300-555-0105', 'diego.moreno@email.com', TRUE),
('Isabella María Jiménez', 'Diagonal 78 #90-12, Montería, Córdoba', '305-555-0106', 'isabella.jimenez@email.com', TRUE),
('Sebastián Andrés Torres', 'Circular 45 #23-56, Sincelejo, Sucre', '318-555-0107', 'sebastian.torres@email.com', TRUE),
('Camila Andrea Vargas', 'Boulevard Central #34-67, Riohacha, La Guajira', '312-555-0108', 'camila.vargas@email.com', FALSE),
('Nicolás David Ruiz', 'Calle de la Paz #78-90, Maicao, La Guajira', '325-555-0109', 'nicolas.ruiz@email.com', TRUE),
('Valentina Sofía Castro', 'Carrera Principal #12-34, Ciénaga, Magdalena', '302-555-0110', 'valentina.castro@email.com', TRUE);

-- Insertar libros de ejemplo (variedad de géneros)
INSERT IGNORE INTO Libro (titulo, autor, isbn, anio_publicacion, disponible) VALUES 
-- Literatura Clásica
('Cien Años de Soledad', 'Gabriel García Márquez', '978-0307474728', 1967, TRUE),
('El Amor en los Tiempos del Cólera', 'Gabriel García Márquez', '978-0307387388', 1985, TRUE),
('Don Quijote de la Mancha', 'Miguel de Cervantes', '978-8437604947', 1605, TRUE),
('La Odisea', 'Homero', '978-8420412146', 800, TRUE),

-- Tecnología y Programación  
('Clean Code', 'Robert C. Martin', '978-0132350884', 2008, TRUE),
('The Pragmatic Programmer', 'Andrew Hunt', '978-0201616224', 1999, TRUE),
('Design Patterns', 'Gang of Four', '978-0201633612', 1994, FALSE),
('Java: The Complete Reference', 'Herbert Schildt', '978-1260440232', 2020, TRUE),
('Spring Boot in Action', 'Craig Walls', '978-1617292545', 2015, TRUE),

-- Ciencia y Matemáticas
('Una Breve Historia del Tiempo', 'Stephen Hawking', '978-0553380163', 1988, TRUE),
('El Gen Egoísta', 'Richard Dawkins', '978-0198788607', 1976, TRUE),
('Cosmos', 'Carl Sagan', '978-0345331359', 1980, FALSE),
('La Estructura de las Revoluciones Científicas', 'Thomas S. Kuhn', '978-0226458083', 1962, TRUE),

-- Historia y Biografías
('Sapiens: De Animales a Dioses', 'Yuval Noah Harari', '978-8499926711', 2011, TRUE),
('Steve Jobs', 'Walter Isaacson', '978-1451648539', 2011, TRUE),
('El Diario de Ana Frank', 'Ana Frank', '978-8497593465', 1947, TRUE),

-- Literatura Contemporánea
('El Código Da Vinci', 'Dan Brown', '978-0307474278', 2003, TRUE),
('Los Juegos del Hambre', 'Suzanne Collins', '978-0439023528', 2008, FALSE),
('Harry Potter y la Piedra Filosofal', 'J.K. Rowling', '978-8498382662', 1997, TRUE),
('1984', 'George Orwell', '978-0452284234', 1949, TRUE),

-- Desarrollo Personal
('Los 7 Hábitos de la Gente Altamente Efectiva', 'Stephen R. Covey', '978-1982137274', 1989, TRUE),
('Piense y Hágase Rico', 'Napoleon Hill', '978-1585424331', 1937, TRUE),

-- Literatura Latinoamericana
('La Casa de los Espíritus', 'Isabel Allende', '978-8497592391', 1982, TRUE),
('Rayuela', 'Julio Cortázar', '978-8437604244', 1963, TRUE),
('Pedro Páramo', 'Juan Rulfo', '978-8437604503', 1955, TRUE),
('Crónica de una Muerte Anunciada', 'Gabriel García Márquez', '978-8497592734', 1981, FALSE);

-- Insertar algunos préstamos de ejemplo
-- Nota: Estos préstamos se insertan con fechas específicas para simular diferentes escenarios

-- Préstamos activos recientes (no vencidos)
INSERT IGNORE INTO Prestamo (libro_id, socio_id, fecha_prestamo, devuelto) VALUES 
((SELECT id FROM Libro WHERE isbn = '978-0132350884'), (SELECT id FROM Socio WHERE email = 'juan.perez@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), FALSE),
((SELECT id FROM Libro WHERE isbn = '978-8499926711'), (SELECT id FROM Socio WHERE email = 'sofia.lopez@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), FALSE),
((SELECT id FROM Libro WHERE isbn = '978-0307474728'), (SELECT id FROM Socio WHERE email = 'laura.garcia@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), FALSE);

-- Préstamos activos vencidos (más de 14 días)
INSERT IGNORE INTO Prestamo (libro_id, socio_id, fecha_prestamo, devuelto, multa_calculada) VALUES 
((SELECT id FROM Libro WHERE isbn = '978-0201633612'), (SELECT id FROM Socio WHERE email = 'miguel.herrera@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), FALSE, 6.00),
((SELECT id FROM Libro WHERE isbn = '978-0345331359'), (SELECT id FROM Socio WHERE email = 'diego.moreno@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), FALSE, 11.00),
((SELECT id FROM Libro WHERE isbn = '978-0439023528'), (SELECT id FROM Socio WHERE email = 'isabella.jimenez@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), FALSE, 4.00);

-- Préstamos devueltos (histórico)
INSERT IGNORE INTO Prestamo (libro_id, socio_id, fecha_prestamo, fecha_devolucion, devuelto, multa_calculada) VALUES 
((SELECT id FROM Libro WHERE isbn = '978-1451648539'), (SELECT id FROM Socio WHERE email = 'sebastian.torres@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), TRUE, 0.00),
((SELECT id FROM Libro WHERE isbn = '978-0553380163'), (SELECT id FROM Socio WHERE email = 'nicolas.ruiz@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), TRUE, 3.00),
((SELECT id FROM Libro WHERE isbn = '978-8497593465'), (SELECT id FROM Socio WHERE email = 'valentina.castro@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 40 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 35 DAY), TRUE, 0.00);

-- Préstamos múltiples del mismo socio (para probar restricciones)
INSERT IGNORE INTO Prestamo (libro_id, socio_id, fecha_prestamo, devuelto) VALUES 
((SELECT id FROM Libro WHERE isbn = '978-1585424331'), (SELECT id FROM Socio WHERE email = 'juan.perez@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), FALSE),
((SELECT id FROM Libro WHERE isbn = '978-8437604947'), (SELECT id FROM Socio WHERE email = 'sofia.lopez@email.com'), DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), FALSE);

-- ====== CONSULTAS DE VERIFICACIÓN ======

-- Mostrar resumen de datos insertados
SELECT '=== RESUMEN DE DATOS INSERTADOS ===' AS info;

SELECT 'USUARIOS' AS tabla, COUNT(*) AS total FROM Usuario
UNION ALL
SELECT 'ROLES', COUNT(*) FROM Rol  
UNION ALL
SELECT 'SOCIOS', COUNT(*) FROM Socio
UNION ALL
SELECT 'LIBROS', COUNT(*) FROM Libro
UNION ALL
SELECT 'PRESTAMOS', COUNT(*) FROM Prestamo
UNION ALL
SELECT 'PRESTAMOS ACTIVOS', COUNT(*) FROM Prestamo WHERE devuelto = FALSE
UNION ALL
SELECT 'PRESTAMOS VENCIDOS', COUNT(*) FROM Vista_Prestamos_Vencidos;

-- Mostrar algunos préstamos para verificación
SELECT '=== PRÉSTAMOS DE EJEMPLO ===' AS info;

SELECT 
    p.id,
    l.titulo,
    s.nombre AS socio,
    p.fecha_prestamo,
    p.devuelto,
    CASE 
        WHEN p.devuelto = FALSE AND DATEDIFF(CURRENT_DATE, p.fecha_prestamo) > 14 
        THEN CONCAT(DATEDIFF(CURRENT_DATE, p.fecha_prestamo) - 14, ' días de retraso')
        ELSE 'En tiempo'
    END AS estado
FROM Prestamo p
JOIN Libro l ON p.libro_id = l.id
JOIN Socio s ON p.socio_id = s.id
ORDER BY p.fecha_prestamo DESC
LIMIT 10;

-- Mostrar libros disponibles y no disponibles
SELECT '=== DISPONIBILIDAD DE LIBROS ===' AS info;

SELECT 
    disponible,
    COUNT(*) AS cantidad
FROM Libro 
GROUP BY disponible;

-- ====== NOTAS IMPORTANTES ======
/*
DATOS INSERTADOS:
✅ 4 usuarios (1 admin + 3 bibliotecarios)
✅ 2 roles (Administrador, Bibliotecario)
✅ 10 socios (1 inactivo para pruebas)
✅ 24 libros (variedad de géneros, algunos no disponibles)
✅ 12 préstamos (activos, vencidos, devueltos)

ESCENARIOS DE PRUEBA CREADOS:
✅ Préstamos activos en tiempo
✅ Préstamos vencidos con multas
✅ Préstamos devueltos (histórico)
✅ Libros no disponibles por estar prestados
✅ Socios con múltiples préstamos
✅ Socio inactivo

PASSWORDS DE USUARIOS:
- admin@novabook.com: admin123
- maria@novabook.com: biblio123
- carlos@novabook.com: biblio123  
- ana@novabook.com: biblio123

Para probar el sistema:
1. Ejecutar schema.sql primero
2. Ejecutar este archivo (sample_data.sql)
3. Los triggers se activarán automáticamente
4. Use las vistas creadas para consultas rápidas
*/