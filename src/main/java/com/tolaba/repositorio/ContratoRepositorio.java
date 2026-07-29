package com.tolaba.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tolaba.enums.EstadoContrato;
import com.tolaba.modelo.Contrato;
import com.tolaba.modelo.Propiedad;

@Repository
public interface ContratoRepositorio extends JpaRepository<Contrato, Long> {

    // HU 3.4: Recupera todos los registros contractuales que no sufrieron baja lógica
    List<Contrato> findByEliminadoFalse();

    // Recupera un contrato específico bloqueando registros eliminados
    Optional<Contrato> findByIdAndEliminadoFalse(Long id);

    // Método de conexión requerido por tus compañeros en PropiedadServicio
    Optional<Contrato> findByPropiedadAndEstadoContratoAndEliminadoFalse(Propiedad propiedad, EstadoContrato estadoContrato);

    // Validación de disponibilidad exclusiva para tu ContratoServicio (Optimizado)
    boolean existsByPropiedadAndEstadoContratoAndEliminadoFalse(Propiedad propiedad, EstadoContrato estadoContrato);

    // Validación de duplicados excluyendo el ID actual en etapas de Modificación (HU 3.3)
    boolean existsByPropiedadAndEstadoContratoAndEliminadoFalseAndIdNot(Propiedad propiedad, EstadoContrato estadoContrato, Long id);
}
