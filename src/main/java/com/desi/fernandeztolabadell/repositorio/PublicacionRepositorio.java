package com.desi.fernandeztolabadell.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.desi.fernandeztolabadell.enums.EstadoPublicacion;
import com.desi.fernandeztolabadell.modelo.Propiedad;
import com.desi.fernandeztolabadell.modelo.Publicacion;

public interface PublicacionRepositorio extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByEliminadoFalse();

    Optional<Publicacion> findByIdAndEliminadoFalse(Long id);

    boolean existsByPropiedadAndEstadoPublicacionAndEliminadoFalse(
            Propiedad propiedad,
            EstadoPublicacion estadoPublicacion);

    boolean existsByPropiedadAndEstadoPublicacionAndEliminadoFalseAndIdNot(
            Propiedad propiedad,
            EstadoPublicacion estadoPublicacion,
            Long id);

    @Query("""
            SELECT p FROM Publicacion p
            WHERE p.eliminado = false
            AND (:propiedadId IS NULL OR p.propiedad.id = :propiedadId)
            AND (:ciudad IS NULL OR LOWER(p.propiedad.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')))
            AND (:estado IS NULL OR p.estadoPublicacion = :estado)
            AND (:precioMin IS NULL OR p.precioMensual >= :precioMin)
            AND (:precioMax IS NULL OR p.precioMensual <= :precioMax)
            """)
    List<Publicacion> filtrar(
            @Param("propiedadId") Long propiedadId,
            @Param("ciudad") String ciudad,
            @Param("estado") EstadoPublicacion estado,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax);
}