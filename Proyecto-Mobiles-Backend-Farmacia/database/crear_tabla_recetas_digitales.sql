-- Crear tabla RecetasDigitales si no existe
CREATE TABLE IF NOT EXISTS RecetasDigitales (
    idRecetaDigital BIGINT AUTO_INCREMENT PRIMARY KEY,
    imagenUrl VARCHAR(500) NOT NULL,
    textoExtraido TEXT,
    estado VARCHAR(50) DEFAULT 'PENDIENTE',
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaProcesamiento TIMESTAMP NULL,
    cliente_id BIGINT,
    medico_id BIGINT,
    FOREIGN KEY (cliente_id) REFERENCES Clientes(idClientes),
    FOREIGN KEY (medico_id) REFERENCES Medicos(idMedicos)
);

-- Crear tabla RecetasDigitalesDetalle si no existe
CREATE TABLE IF NOT EXISTS RecetasDigitalesDetalle (
    idDetalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicamentoTexto VARCHAR(500),
    productoId BIGINT,
    validado BOOLEAN DEFAULT FALSE,
    recetaDigital_id BIGINT,
    FOREIGN KEY (recetaDigital_id) REFERENCES RecetasDigitales(idRecetaDigital),
    FOREIGN KEY (productoId) REFERENCES Productos(idProductos)
);

-- Verificar que las tablas existen
SHOW TABLES LIKE 'RecetasDigitales%';