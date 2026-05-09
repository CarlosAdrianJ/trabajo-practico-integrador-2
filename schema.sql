

CREATE DATABASE IF NOT EXISTS basedatos_tpi2
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE basedatos_tpi2;



DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS categoria;


CREATE TABLE categoria (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           descripcion VARCHAR(255),
                           eliminado BOOLEAN NOT NULL DEFAULT FALSE,
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT uk_categoria_nombre UNIQUE (nombre)
);



CREATE TABLE producto (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          precio DECIMAL(10,2) NOT NULL,
                          descripcion VARCHAR(255),
                          stock INT NOT NULL,
                          imagen VARCHAR(255),
                          disponible BOOLEAN NOT NULL DEFAULT TRUE,
                          categoria_id BIGINT NOT NULL,
                          eliminado BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_producto_categoria
                              FOREIGN KEY (categoria_id)
                                  REFERENCES categoria(id),

                          CONSTRAINT chk_producto_precio
                              CHECK (precio >= 0),

                          CONSTRAINT chk_producto_stock
                              CHECK (stock >= 0)
);



CREATE TABLE usuario (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         apellido VARCHAR(100) NOT NULL,
                         mail VARCHAR(150) NOT NULL,
                         celular VARCHAR(50),
                         contrasena VARCHAR(255) NOT NULL,
                         rol VARCHAR(30) NOT NULL,
                         eliminado BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT uk_usuario_mail UNIQUE (mail)
);



CREATE TABLE pedido (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        fecha DATE NOT NULL,
                        estado VARCHAR(30) NOT NULL,
                        total DECIMAL(10,2) NOT NULL DEFAULT 0,
                        forma_pago VARCHAR(30) NOT NULL,
                        usuario_id BIGINT NOT NULL,
                        eliminado BOOLEAN NOT NULL DEFAULT FALSE,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_pedido_usuario
                            FOREIGN KEY (usuario_id)
                                REFERENCES usuario(id),

                        CONSTRAINT chk_pedido_total
                            CHECK (total >= 0)
);


CREATE TABLE detalle_pedido (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                pedido_id BIGINT NOT NULL,
                                producto_id BIGINT NOT NULL,
                                cantidad INT NOT NULL,
                                subtotal DECIMAL(10,2) NOT NULL,
                                eliminado BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_detalle_pedido_pedido
                                    FOREIGN KEY (pedido_id)
                                        REFERENCES pedido(id),

                                CONSTRAINT fk_detalle_pedido_producto
                                    FOREIGN KEY (producto_id)
                                        REFERENCES producto(id),

                                CONSTRAINT chk_detalle_cantidad
                                    CHECK (cantidad > 0),

                                CONSTRAINT chk_detalle_subtotal
                                    CHECK (subtotal >= 0)
);



INSERT INTO categoria (nombre, descripcion)
VALUES
    ('Hamburguesas', 'Productos principales de hamburguesas'),
    ('Bebidas', 'Bebidas frías y calientes'),
    ('Postres', 'Opciones dulces');

INSERT INTO producto (nombre, precio, descripcion, stock, imagen, disponible, categoria_id)
VALUES
    ('Hamburguesa simple', 4500.00, 'Hamburguesa con carne, queso y pan', 20, 'hamburguesa_simple.jpg', TRUE, 1),
    ('Coca Cola', 1500.00, 'Gaseosa individual', 50, 'coca_cola.jpg', TRUE, 2),
    ('Brownie', 2000.00, 'Postre de chocolate', 15, 'brownie.jpg', TRUE, 3);

INSERT INTO usuario (nombre, apellido, mail, celular, contrasena, rol)
VALUES
    ('Admin', 'Sistema', 'admin@foodstore.com', '0000000000', 'admin123', 'ADMIN'),
    ('Juan', 'Pérez', 'juan@correo.com', '1111111111', 'usuario123', 'USUARIO');