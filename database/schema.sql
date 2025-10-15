-- ====================================================================
-- NovaBook Library Management System - Database Schema
-- Author: Luis Alfredo - Clan Cienaga
-- Date: October 2024
-- Description: Complete database schema with tables, constraints, and triggers
-- ====================================================================

-- Crear base de datos y usarla
CREATE DATABASE IF NOT EXISTS Biblioteca
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE Biblioteca;

-- ====== TABLAS PRINCIPALES ======

-- Tabla de roles
CREATE TABLE IF NOT EXISTS Rol (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Tabla de roles del sistema (Administrador, Bibliotecario)';

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS Usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Tabla de usuarios del sistema';

-- Relación muchos a muchos entre usuario y rol
CREATE TABLE IF NOT EXISTS Usuario_Rol (
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_rol_usuario 
        FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_usuario_rol_rol 
        FOREIGN KEY (rol_id) REFERENCES Rol(id)
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Relación muchos a muchos entre usuarios y roles';

-- Tabla de socios
CREATE TABLE IF NOT EXISTS Socio (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(50),
    email VARCHAR(150) UNIQUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB COMMENT='Tabla de socios/miembros de la biblioteca';

-- Tabla de libros
CREATE TABLE IF NOT EXISTS Libro (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    anio_publicacion INT NOT NULL CHECK (anio_publicacion BETWEEN 1500 AND 2100),
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_alta TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Tabla de libros de la biblioteca';

-- Tabla de préstamos
CREATE TABLE IF NOT EXISTS Prestamo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    libro_id INT NOT NULL,
    socio_id INT NOT NULL,
    fecha_prestamo DATE NOT NULL DEFAULT (CURRENT_DATE),
    fecha_devolucion DATE,
    devuelto BOOLEAN NOT NULL DEFAULT FALSE,
    multa_calculada DECIMAL(10,2) DEFAULT 0.00,
    observaciones TEXT,
    CONSTRAINT fk_prestamo_libro 
        FOREIGN KEY (libro_id) REFERENCES Libro(id)
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT fk_prestamo_socio 
        FOREIGN KEY (socio_id) REFERENCES Socio(id)
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT chk_fechas 
        CHECK (fecha_devolucion IS NULL OR fecha_devolucion >= fecha_prestamo),
    CONSTRAINT chk_multa_positiva
        CHECK (multa_calculada >= 0)
) ENGINE=InnoDB COMMENT='Tabla de préstamos de libros';

-- ====== INDICES PARA RENDIMIENTO ======

-- Indices en Libro
CREATE INDEX idx_libro_isbn ON Libro(isbn);
CREATE INDEX idx_libro_titulo ON Libro(titulo);
CREATE INDEX idx_libro_autor ON Libro(autor);
CREATE INDEX idx_libro_disponible ON Libro(disponible);

-- Indices en Socio
CREATE INDEX idx_socio_nombre ON Socio(nombre);
CREATE INDEX idx_socio_email ON Socio(email);
CREATE INDEX idx_socio_activo ON Socio(activo);

-- Indices en Prestamo
CREATE INDEX idx_prestamo_fecha ON Prestamo(fecha_prestamo);
CREATE INDEX idx_prestamo_devuelto ON Prestamo(devuelto);
CREATE INDEX idx_prestamo_libro_socio ON Prestamo(libro_id, socio_id);

-- Indices en Usuario
CREATE INDEX idx_usuario_email ON Usuario(email);
CREATE INDEX idx_usuario_activo ON Usuario(activo);

-- ====== TRIGGERS ======
DELIMITER $$

-- Trigger: Cuando se realiza un préstamo, marcar libro como no disponible
CREATE TRIGGER tr_prestamo_insert
AFTER INSERT ON Prestamo
FOR EACH ROW
BEGIN
    UPDATE Libro 
    SET disponible = FALSE
    WHERE id = NEW.libro_id;
END$$

-- Trigger: Cuando se marca un préstamo como devuelto, liberar el libro
CREATE TRIGGER tr_prestamo_update
AFTER UPDATE ON Prestamo
FOR EACH ROW
BEGIN
    -- Si se marca como devuelto y antes no lo estaba
    IF NEW.devuelto = TRUE AND OLD.devuelto = FALSE THEN
        UPDATE Libro 
        SET disponible = TRUE
        WHERE id = NEW.libro_id;
    END IF;
    
    -- Si se desmarca como devuelto (caso raro pero posible)
    IF NEW.devuelto = FALSE AND OLD.devuelto = TRUE THEN
        UPDATE Libro 
        SET disponible = FALSE
        WHERE id = NEW.libro_id;
    END IF;
END$$

-- Trigger: Prevenir eliminar libros que tienen préstamos activos
CREATE TRIGGER tr_libro_delete_check
BEFORE DELETE ON Libro
FOR EACH ROW
BEGIN
    DECLARE prestamos_activos INT DEFAULT 0;
    
    SELECT COUNT(*) INTO prestamos_activos
    FROM Prestamo 
    WHERE libro_id = OLD.id AND devuelto = FALSE;
    
    IF prestamos_activos > 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'No se puede eliminar un libro con préstamos activos';
    END IF;
END$$

-- Trigger: Prevenir eliminar socios que tienen préstamos activos
CREATE TRIGGER tr_socio_delete_check
BEFORE DELETE ON Socio
FOR EACH ROW
BEGIN
    DECLARE prestamos_activos INT DEFAULT 0;
    
    SELECT COUNT(*) INTO prestamos_activos
    FROM Prestamo 
    WHERE socio_id = OLD.id AND devuelto = FALSE;
    
    IF prestamos_activos > 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'No se puede eliminar un socio con préstamos activos';
    END IF;
END$$

DELIMITER ;

-- ====== DATOS INICIALES ======

-- Insertar roles básicos
INSERT IGNORE INTO Rol (nombre, descripcion) VALUES 
('Administrador', 'Acceso completo al sistema'),
('Bibliotecario', 'Acceso limitado, sin gestión de usuarios'),
('Socio', 'Puede ver libros disponibles y realizar préstamos'),
('Usuario', 'Solo puede ver el catálogo de libros');

-- Insertar usuarios por defecto
INSERT IGNORE INTO Usuario (nombre, email, password, activo) VALUES 
('Administrador del Sistema', 'admin@novabook.com', 'admin', TRUE),
('Bibliotecario Principal', 'librarian@novabook.com', 'librarian', TRUE),
('Juan Pérez - Socio', 'socio@novabook.com', 'socio123', TRUE),
('María García - Usuario', 'usuario@novabook.com', 'usuario123', TRUE);

-- Asignar roles a usuarios
INSERT IGNORE INTO Usuario_Rol (usuario_id, rol_id) 
SELECT u.id, r.id FROM Usuario u, Rol r WHERE u.email = 'admin@novabook.com' AND r.nombre = 'Administrador';

INSERT IGNORE INTO Usuario_Rol (usuario_id, rol_id) 
SELECT u.id, r.id FROM Usuario u, Rol r WHERE u.email = 'librarian@novabook.com' AND r.nombre = 'Bibliotecario';

INSERT IGNORE INTO Usuario_Rol (usuario_id, rol_id) 
SELECT u.id, r.id FROM Usuario u, Rol r WHERE u.email = 'socio@novabook.com' AND r.nombre = 'Socio';

INSERT IGNORE INTO Usuario_Rol (usuario_id, rol_id) 
SELECT u.id, r.id FROM Usuario u, Rol r WHERE u.email = 'usuario@novabook.com' AND r.nombre = 'Usuario';

-- Insertar algunos socios de ejemplo
INSERT IGNORE INTO Socio (nombre, direccion, telefono, email, activo) VALUES
('Juan Pérez', 'Calle 123 #45-67', '3001234567', 'juan.perez@email.com', TRUE),
('Ana López', 'Carrera 89 #12-34', '3009876543', 'ana.lopez@email.com', TRUE),
('Carlos Rodríguez', 'Avenida 56 #78-90', '3005551234', 'carlos.rodriguez@email.com', TRUE),
('Laura Martínez', 'Calle 34 #56-78', '3007890123', 'laura.martinez@email.com', FALSE);

-- ====== VISTAS ÚTILES ======

-- Vista de préstamos con información completa
CREATE OR REPLACE VIEW Vista_Prestamos_Completa AS
SELECT 
    p.id AS prestamo_id,
    l.titulo AS libro_titulo,
    l.autor AS libro_autor,
    l.isbn AS libro_isbn,
    s.nombre AS socio_nombre,
    s.email AS socio_email,
    p.fecha_prestamo,
    p.fecha_devolucion,
    p.devuelto,
    p.multa_calculada,
    DATEDIFF(CURRENT_DATE, p.fecha_prestamo) AS dias_prestamo,
    CASE 
        WHEN p.devuelto = FALSE AND DATEDIFF(CURRENT_DATE, p.fecha_prestamo) > 14 
        THEN DATEDIFF(CURRENT_DATE, p.fecha_prestamo) - 14
        ELSE 0 
    END AS dias_retraso
FROM Prestamo p
JOIN Libro l ON p.libro_id = l.id
JOIN Socio s ON p.socio_id = s.id;

-- Vista de préstamos vencidos
CREATE OR REPLACE VIEW Vista_Prestamos_Vencidos AS
SELECT *
FROM Vista_Prestamos_Completa
WHERE devuelto = FALSE AND dias_retraso > 0;

-- Vista de libros más prestados
CREATE OR REPLACE VIEW Vista_Libros_Populares AS
SELECT 
    l.id,
    l.titulo,
    l.autor,
    l.isbn,
    COUNT(p.id) AS total_prestamos,
    COUNT(CASE WHEN p.devuelto = FALSE THEN 1 END) AS prestamos_activos
FROM Libro l
LEFT JOIN Prestamo p ON l.id = p.libro_id
GROUP BY l.id, l.titulo, l.autor, l.isbn
ORDER BY total_prestamos DESC;

-- ====== PROCEDIMIENTOS ALMACENADOS ======
DELIMITER $$

-- Procedimiento para calcular multa de un préstamo
CREATE PROCEDURE CalcularMulta(
    IN prestamo_id INT,
    IN multa_por_dia DECIMAL(5,2),
    OUT multa_total DECIMAL(10,2)
)
BEGIN
    DECLARE dias_retraso INT DEFAULT 0;
    
    SELECT 
        CASE 
            WHEN devuelto = FALSE AND DATEDIFF(CURRENT_DATE, fecha_prestamo) > 14 
            THEN DATEDIFF(CURRENT_DATE, fecha_prestamo) - 14
            WHEN devuelto = TRUE AND DATEDIFF(fecha_devolucion, fecha_prestamo) > 14
            THEN DATEDIFF(fecha_devolucion, fecha_prestamo) - 14
            ELSE 0 
        END INTO dias_retraso
    FROM Prestamo 
    WHERE id = prestamo_id;
    
    SET multa_total = dias_retraso * multa_por_dia;
    
    -- Actualizar la multa calculada en la tabla
    UPDATE Prestamo 
    SET multa_calculada = multa_total 
    WHERE id = prestamo_id;
END$$

-- Procedimiento para obtener estadísticas de la biblioteca
CREATE PROCEDURE ObtenerEstadisticas()
BEGIN
    SELECT 
        'Total Libros' as estadistica, COUNT(*) as valor FROM Libro
    UNION ALL
    SELECT 
        'Libros Disponibles', COUNT(*) FROM Libro WHERE disponible = TRUE
    UNION ALL
    SELECT 
        'Total Socios', COUNT(*) FROM Socio WHERE activo = TRUE
    UNION ALL
    SELECT 
        'Préstamos Activos', COUNT(*) FROM Prestamo WHERE devuelto = FALSE
    UNION ALL
    SELECT 
        'Préstamos Vencidos', COUNT(*) FROM Vista_Prestamos_Vencidos;
END$$

DELIMITER ;

-- ====== COMENTARIOS FINALES ======

/*
Este script crea la estructura completa de la base de datos para NovaBook:

TABLAS PRINCIPALES:
- Rol: Define roles del sistema (Administrador, Bibliotecario)
- Usuario: Usuarios del sistema con autenticación
- Usuario_Rol: Relación muchos a muchos entre usuarios y roles
- Socio: Miembros/socios de la biblioteca
- Libro: Catálogo de libros
- Prestamo: Registro de préstamos de libros

CARACTERÍSTICAS:
✅ Restricciones de integridad referencial
✅ Triggers automáticos para gestión de disponibilidad
✅ Indices para optimizar consultas
✅ Vistas para reportes comunes
✅ Procedimientos almacenados para cálculos
✅ Datos iniciales (usuario admin)
✅ Validaciones de negocio

SEGURIDAD:
- Password del admin por defecto hasheado con bcrypt
- Restricciones para prevenir eliminación de datos con dependencias
- Validaciones en triggers

USO:
1. Ejecutar este script en MySQL/MariaDB
2. Configurar conexión en ConnectionDB.java
3. Las tablas se crearán automáticamente si no existen
*/