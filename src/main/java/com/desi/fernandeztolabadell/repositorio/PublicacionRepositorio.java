package com.desi.fernandeztolabadell.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.desi.fernandeztolabadell.enums.EstadoPublicacion;
import com.desi.fernandeztolabadell.modelo.Propiedad;
import com.desi.fernandeztolabadell.modelo.Publicacion;

public interface PublicacionRepositorio extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByEliminadoFalse();

    Optional<Publicacion> findByPropiedadAndEstadoPublicacionAndEliminadoFalse(
            Propiedad propiedad,
            EstadoPublicacion estadoPublicacion
    );

    List<Publicacion> findByEstadoPublicacionAndEliminadoFalse(EstadoPublicacion estadoPublicacion);

    List<Publicacion> findByPropiedadCiudadContainingIgnoreCaseAndEliminadoFalse(String ciudad);
}