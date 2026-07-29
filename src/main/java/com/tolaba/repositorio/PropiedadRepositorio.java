package com.tolaba.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tolaba.enums.EstadoDisponibilidad;
import com.tolaba.enums.TipoPropiedad;
import com.tolaba.modelo.Propiedad;

public interface PropiedadRepositorio extends JpaRepository<Propiedad, Long> {

    List<Propiedad> findByEliminadoFalse();

    Optional<Propiedad> findByDireccionIgnoreCaseAndCiudadIgnoreCaseAndEliminadoFalse(String direccion, String ciudad);

    List<Propiedad> findByEstadoDisponibilidadAndEliminadoFalse(EstadoDisponibilidad estadoDisponibilidad);

    List<Propiedad> findByTipoPropiedadAndEliminadoFalse(TipoPropiedad tipoPropiedad);

    List<Propiedad> findByCiudadContainingIgnoreCaseAndEliminadoFalse(String ciudad);

    List<Propiedad> findByDireccionContainingIgnoreCaseAndEliminadoFalse(String direccion);
}