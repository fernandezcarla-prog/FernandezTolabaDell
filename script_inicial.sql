CREATE DATABASE IF NOT EXISTS fernandeztolabadell;

USE fernandeztolabadell;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE historial_estados_contrato;
TRUNCATE TABLE historial_estados_publicacion;
TRUNCATE TABLE historial_estados_propiedad;
TRUNCATE TABLE contratos;
TRUNCATE TABLE publicaciones;
TRUNCATE TABLE propiedades;
TRUNCATE TABLE personas;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO personas (id, nombre, apellido, email, telefono, eliminado) VALUES
(1, 'Carla', 'Fernandez', '[cfernandezbenitez@frsf.utn.edu.ar](mailto:cfernandezbenitez@frsf.utn.edu.ar)', '3425000001', 0),
(2, 'Oscar', 'Dell', '[odell@frsf.utn.edu.ar](mailto:odell@frsf.utn.edu.ar)', '3425000002', 0),
(3, 'Marcelo', 'Tolaba', '[martolaba@abc.gob.ar](mailto:martolaba@abc.gob.ar)', '3425000003', 0),
(4, 'Juan', 'Perez', '[juan.perez@mail.com](mailto:juan.perez@mail.com)', '3425000004', 0),
(5, 'Ana', 'Gomez', '[ana.gomez@mail.com](mailto:ana.gomez@mail.com)', '3425000005', 0);

INSERT INTO propiedades (
id,
direccion,
ciudad,
tipo_propiedad,
cantidad_ambientes,
metros_cuadrados,
descripcion,
estado_disponibilidad,
propietario_id,
eliminado
) VALUES
(1, 'San Martin 1234', 'Santa Fe', 'CASA', 3, 80.0, 'Casa ubicada en zona céntrica.', 'DISPONIBLE', 1, 0),
(2, 'Belgrano 2500', 'Santa Fe', 'DEPARTAMENTO', 2, 45.0, 'Departamento de dos ambientes.', 'DISPONIBLE', 2, 0),
(3, 'Av. Freyre 1800', 'Santa Fe', 'LOCAL', 1, 60.0, 'Local comercial sobre avenida.', 'DISPONIBLE', 3, 0),
(4, 'Urquiza 900', 'Paraná', 'CASA', 4, 120.0, 'Casa familiar con patio.', 'DISPONIBLE', 1, 0);

INSERT INTO publicaciones (
id,
propiedad_id,
precio_mensual,
condiciones_alquiler,
descripcion,
fecha_publicacion,
estado_publicacion,
eliminado
) VALUES
(1, 1, 150000.00, 'Mes de depósito y garantía propietaria.', 'Publicación de casa en Santa Fe.', '2026-06-10', 'ACTIVA', 0),
(2, 2, 120000.00, 'Mes de depósito y recibo de sueldo.', 'Publicación de departamento céntrico.', '2026-06-10', 'ACTIVA', 0),
(3, 3, 200000.00, 'Contrato comercial con garantía.', 'Publicación de local comercial.', '2026-06-10', 'PAUSADA', 0);

INSERT INTO contratos (
id,
propiedad_id,
inquilino_id,
fecha_inicio,
duracion_meses,
importe_mensual,
dia_vencimiento_mensual,
descripcion,
estado_contrato,
eliminado
) VALUES
(1, 4, 4, '2026-06-10', 24, 180000.00, 10, 'Contrato de alquiler de prueba para casa en Paraná.', 'BORRADOR', 0),
(2, 2, 5, '2026-06-15', 12, 120000.00, 15, 'Contrato de alquiler de prueba para departamento.', 'BORRADOR', 0);
