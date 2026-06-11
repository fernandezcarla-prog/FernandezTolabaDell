package com.desi.fernandeztolabadell.servicio;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desi.fernandeztolabadell.enums.EstadoDisponibilidad;
import com.desi.fernandeztolabadell.enums.EstadoPublicacion;
import com.desi.fernandeztolabadell.modelo.HistorialEstadoPublicacion;
import com.desi.fernandeztolabadell.modelo.Propiedad;
import com.desi.fernandeztolabadell.modelo.Publicacion;
import com.desi.fernandeztolabadell.repositorio.HistorialEstadoPublicacionRepositorio;
import com.desi.fernandeztolabadell.repositorio.PropiedadRepositorio;
import com.desi.fernandeztolabadell.repositorio.PublicacionRepositorio;

@Service
public class PublicacionServicio {

    private final PublicacionRepositorio publicacionRepositorio;
    private final PropiedadRepositorio propiedadRepositorio;
    private final HistorialEstadoPublicacionRepositorio historialEstadoPublicacionRepositorio;

    public PublicacionServicio(
            PublicacionRepositorio publicacionRepositorio,
            PropiedadRepositorio propiedadRepositorio,
            HistorialEstadoPublicacionRepositorio historialEstadoPublicacionRepositorio) {
        this.publicacionRepositorio = publicacionRepositorio;
        this.propiedadRepositorio = propiedadRepositorio;
        this.historialEstadoPublicacionRepositorio = historialEstadoPublicacionRepositorio;
    }

    public List<Publicacion> listarNoEliminadas() {
        return publicacionRepositorio.findByEliminadoFalse();
    }

    public Publicacion buscarPorId(Long id) {
        return publicacionRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la publicación indicada"));
    }

    @Transactional
    public Publicacion crear(Publicacion publicacion) {
        Propiedad propiedad = propiedadRepositorio.findById(publicacion.getPropiedad().getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe la propiedad indicada"));

        validarPropiedadPublicable(propiedad);
        validarSinPublicacionActiva(propiedad, null);

        if (publicacion.getEstadoPublicacion() == null) {
            publicacion.setEstadoPublicacion(EstadoPublicacion.ACTIVA);
        }

        publicacion.setPropiedad(propiedad);
        publicacion.setEliminado(false);

        Publicacion publicacionGuardada = publicacionRepositorio.save(publicacion);

        historialEstadoPublicacionRepositorio.save(
                new HistorialEstadoPublicacion(
                        publicacionGuardada,
                        null,
                        publicacionGuardada.getEstadoPublicacion()
                )
        );

        return publicacionGuardada;
    }

    @Transactional
    public Publicacion modificar(Long id, Publicacion datosNuevos) {
        Publicacion publicacionActual = buscarPorId(id);

        EstadoPublicacion estadoAnterior = publicacionActual.getEstadoPublicacion();
        EstadoPublicacion estadoNuevo = datosNuevos.getEstadoPublicacion();

        if (estadoNuevo == EstadoPublicacion.ACTIVA) {
            validarPropiedadPublicable(publicacionActual.getPropiedad());
            validarSinPublicacionActiva(publicacionActual.getPropiedad(), id);
        }

        if (publicacionActual.getEstadoPublicacion() == EstadoPublicacion.FINALIZADA
                && !Objects.equals(publicacionActual.getCondicionesAlquiler(), datosNuevos.getCondicionesAlquiler())) {
            throw new IllegalArgumentException("No se pueden modificar las condiciones de una publicación finalizada");
        }

        publicacionActual.setPrecioMensual(datosNuevos.getPrecioMensual());
        publicacionActual.setCondicionesAlquiler(datosNuevos.getCondicionesAlquiler());
        publicacionActual.setDescripcion(datosNuevos.getDescripcion());
        publicacionActual.setFechaPublicacion(datosNuevos.getFechaPublicacion());
        publicacionActual.setEstadoPublicacion(estadoNuevo);

        Publicacion publicacionGuardada = publicacionRepositorio.save(publicacionActual);

        if (estadoAnterior != estadoNuevo) {
            historialEstadoPublicacionRepositorio.save(
                    new HistorialEstadoPublicacion(publicacionGuardada, estadoAnterior, estadoNuevo)
            );
        }

        return publicacionGuardada;
    }

    @Transactional
    public void eliminar(Long id) {
        Publicacion publicacion = buscarPorId(id);

        if (publicacion.getEstadoPublicacion() != EstadoPublicacion.ACTIVA) {
            throw new IllegalArgumentException("Solo pueden eliminarse publicaciones activas");
        }

        publicacion.setEliminado(true);
        publicacionRepositorio.save(publicacion);
    }

    private void validarPropiedadPublicable(Propiedad propiedad) {
        if (Boolean.TRUE.equals(propiedad.getEliminado())) {
            throw new IllegalArgumentException("No se puede publicar una propiedad eliminada");
        }

        if (propiedad.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
            throw new IllegalArgumentException("Solo se pueden publicar propiedades disponibles");
        }
    }

    private void validarSinPublicacionActiva(Propiedad propiedad, Long idPublicacionActual) {
        Optional<Publicacion> publicacionActiva = publicacionRepositorio
                .findByPropiedadAndEstadoPublicacionAndEliminadoFalse(propiedad, EstadoPublicacion.ACTIVA);

        if (publicacionActiva.isPresent()
                && (idPublicacionActual == null || !publicacionActiva.get().getId().equals(idPublicacionActual))) {
            throw new IllegalArgumentException("Ya existe una publicación activa para esta propiedad");
        }
    }
}