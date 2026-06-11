package com.desi.fernandeztolabadell.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.desi.fernandeztolabadell.enums.EstadoContrato;
import com.desi.fernandeztolabadell.modelo.Contrato;
import com.desi.fernandeztolabadell.modelo.Persona;
import com.desi.fernandeztolabadell.modelo.Propiedad;

public interface ContratoRepositorio extends JpaRepository<Contrato, Long> {

    List<Contrato> findByEliminadoFalse();

    Optional<Contrato> findByPropiedadAndEstadoContratoAndEliminadoFalse(
            Propiedad propiedad,
            EstadoContrato estadoContrato
    );

    List<Contrato> findByEstadoContratoAndEliminadoFalse(EstadoContrato estadoContrato);

    List<Contrato> findByInquilinoAndEliminadoFalse(Persona inquilino);

    List<Contrato> findByPropiedadAndEliminadoFalse(Propiedad propiedad);
}
