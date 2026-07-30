package com.desi.fernandeztolabadell.servicio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.desi.fernandeztolabadell.enums.EstadoDisponibilidad;
import com.desi.fernandeztolabadell.enums.EstadoPublicacion;
import com.desi.fernandeztolabadell.modelo.HistorialEstadoPublicacion;
import com.desi.fernandeztolabadell.modelo.Propiedad;
import com.desi.fernandeztolabadell.modelo.Publicacion;
import com.desi.fernandeztolabadell.repositorio.HistorialEstadoPublicacionRepositorio;
import com.desi.fernandeztolabadell.repositorio.PublicacionRepositorio;

@Service
public class PublicacionServicio {

    private final PublicacionRepositorio publicacionRepositorio;
    private final HistorialEstadoPublicacionRepositorio historialEstadoPublicacionRepositorio;

    public PublicacionServicio(
            PublicacionRepositorio publicacionRepositorio,
            HistorialEstadoPublicacionRepositorio historialEstadoPublicacionRepositorio) {
        this.publicacionRepositorio = publicacionRepositorio;
        this.historialEstadoPublicacionRepositorio = historialEstadoPublicacionRepositorio;
    }

    public List<Publicacion> listarNoEliminadas() {
        return publicacionRepositorio.findByEliminadoFalse();
    }

    public List<Publicacion> filtrar(
            Long propiedadId,
            String ciudad,
            EstadoPublicacion estado,
            Double precioMin,
            Double precioMax) {

        if (ciudad != null && ciudad.isBlank()) {
            ciudad = null;
        }

        return publicacionRepositorio.filtrar(propiedadId, ciudad, estado, precioMin, precioMax);
    }

    public Publicacion buscarPorId(Long id) {
        return publicacionRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Publicación no encontrada"));
    }

    public Publicacion guardar(Publicacion publicacion) {
        if (publicacion.getEstadoPublicacion() == null) {
            publicacion.setEstadoPublicacion(EstadoPublicacion.ACTIVA);
        }

        if (publicacion.getEliminado() == null) {
            publicacion.setEliminado(false);
        }

        if (publicacion.getEstadoPublicacion() == EstadoPublicacion.ACTIVA) {
            validarPuedeEstarActiva(publicacion, null);
        }

        Publicacion guardada = publicacionRepositorio.save(publicacion);
        registrarCambioEstado(guardada, null, guardada.getEstadoPublicacion());

        return guardada;
    }

    public Publicacion actualizar(Long id, Publicacion datosActualizados) {
        Publicacion publicacion = buscarPorId(id);

        EstadoPublicacion estadoAnterior = publicacion.getEstadoPublicacion();

        if (publicacion.getEstadoPublicacion() == EstadoPublicacion.FINALIZADA) {
            boolean cambioCondiciones = !publicacion.getCondicionesAlquiler()
                    .equals(datosActualizados.getCondicionesAlquiler());

            if (cambioCondiciones) {
                throw new IllegalArgumentException(
                        "No se pueden modificar las condiciones de alquiler de una publicación finalizada");
            }
        }

        publicacion.setPrecioMensual(datosActualizados.getPrecioMensual());
        publicacion.setCondicionesAlquiler(datosActualizados.getCondicionesAlquiler());
        publicacion.setDescripcion(datosActualizados.getDescripcion());
        publicacion.setFechaPublicacion(datosActualizados.getFechaPublicacion());
        publicacion.setEstadoPublicacion(datosActualizados.getEstadoPublicacion());

        if (datosActualizados.getPropiedad() != null) {
            publicacion.setPropiedad(datosActualizados.getPropiedad());
        }

        if (publicacion.getEstadoPublicacion() == EstadoPublicacion.ACTIVA) {
            validarPuedeEstarActiva(publicacion, id);
        }

        Publicacion guardada = publicacionRepositorio.save(publicacion);

        if (estadoAnterior != guardada.getEstadoPublicacion()) {
            registrarCambioEstado(guardada, estadoAnterior, guardada.getEstadoPublicacion());
        }

        return guardada;
    }

    public void eliminar(Long id) {
        Publicacion publicacion = buscarPorId(id);

        if (publicacion.getEstadoPublicacion() != EstadoPublicacion.ACTIVA) {
            throw new IllegalArgumentException("Solo pueden eliminarse publicaciones activas");
        }

        publicacion.setEliminado(true);
        publicacionRepositorio.save(publicacion);
    }

    private void validarPuedeEstarActiva(Publicacion publicacion, Long idActual) {
        Propiedad propiedad = publicacion.getPropiedad();

        if (propiedad == null) {
            throw new IllegalArgumentException("Debe seleccionar una propiedad");
        }

        if (Boolean.TRUE.equals(propiedad.getEliminado())) {
            throw new IllegalArgumentException("No se puede publicar una propiedad eliminada");
        }

        if (propiedad.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
            throw new IllegalArgumentException("Solo se pueden publicar propiedades disponibles");
        }

        boolean existePublicacionActiva;

        if (idActual == null) {
            existePublicacionActiva = publicacionRepositorio
                    .existsByPropiedadAndEstadoPublicacionAndEliminadoFalse(
                            propiedad,
                            EstadoPublicacion.ACTIVA);
        } else {
            existePublicacionActiva = publicacionRepositorio
                    .existsByPropiedadAndEstadoPublicacionAndEliminadoFalseAndIdNot(
                            propiedad,
                            EstadoPublicacion.ACTIVA,
                            idActual);
        }

        if (existePublicacionActiva) {
            throw new IllegalArgumentException(
                    "Ya existe una publicación activa para esa propiedad");
        }
    }

    private void registrarCambioEstado(
            Publicacion publicacion,
            EstadoPublicacion estadoAnterior,
            EstadoPublicacion estadoNuevo) {

        HistorialEstadoPublicacion historial = new HistorialEstadoPublicacion();
        historial.setPublicacion(publicacion);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setFechaCambio(LocalDateTime.now());

        historialEstadoPublicacionRepositorio.save(historial);
    }
}