package com.desi.fernandeztolabadell.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desi.fernandeztolabadell.enums.EstadoContrato;
import com.desi.fernandeztolabadell.enums.EstadoDisponibilidad;
import com.desi.fernandeztolabadell.modelo.HistorialEstadoPropiedad;
import com.desi.fernandeztolabadell.modelo.Propiedad;
import com.desi.fernandeztolabadell.repositorio.ContratoRepositorio;
import com.desi.fernandeztolabadell.repositorio.HistorialEstadoPropiedadRepositorio;
import com.desi.fernandeztolabadell.repositorio.PropiedadRepositorio;

@Service
public class PropiedadServicio {

    private final PropiedadRepositorio propiedadRepositorio;
    private final ContratoRepositorio contratoRepositorio;
    private final HistorialEstadoPropiedadRepositorio historialEstadoPropiedadRepositorio;

    public PropiedadServicio(
            PropiedadRepositorio propiedadRepositorio,
            ContratoRepositorio contratoRepositorio,
            HistorialEstadoPropiedadRepositorio historialEstadoPropiedadRepositorio) {
        this.propiedadRepositorio = propiedadRepositorio;
        this.contratoRepositorio = contratoRepositorio;
        this.historialEstadoPropiedadRepositorio = historialEstadoPropiedadRepositorio;
    }

    public List<Propiedad> listarNoEliminadas() {
        return propiedadRepositorio.findByEliminadoFalse();
    }

    public Propiedad buscarPorId(Long id) {
        return propiedadRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la propiedad indicada"));
    }

    @Transactional
    public Propiedad crear(Propiedad propiedad) {
        validarDuplicado(propiedad);

        if (propiedad.getEstadoDisponibilidad() == null) {
            propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        }

        propiedad.setEliminado(false);
        Propiedad propiedadGuardada = propiedadRepositorio.save(propiedad);

        historialEstadoPropiedadRepositorio.save(
                new HistorialEstadoPropiedad(
                        propiedadGuardada,
                        null,
                        propiedadGuardada.getEstadoDisponibilidad()
                )
        );

        return propiedadGuardada;
    }

    @Transactional
    public Propiedad modificar(Long id, Propiedad datosNuevos) {
        Propiedad propiedadActual = buscarPorId(id);

        Optional<Propiedad> duplicada = propiedadRepositorio
                .findByDireccionIgnoreCaseAndCiudadIgnoreCaseAndEliminadoFalse(
                        datosNuevos.getDireccion(),
                        datosNuevos.getCiudad()
                );

        if (duplicada.isPresent() && !duplicada.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe una propiedad activa con esa dirección y ciudad");
        }

        EstadoDisponibilidad estadoAnterior = propiedadActual.getEstadoDisponibilidad();
        EstadoDisponibilidad estadoNuevo = datosNuevos.getEstadoDisponibilidad();

        boolean tieneContratoActivo = contratoRepositorio
                .findByPropiedadAndEstadoContratoAndEliminadoFalse(propiedadActual, EstadoContrato.ACTIVO)
                .isPresent();

        if (tieneContratoActivo &&
                (estadoNuevo == EstadoDisponibilidad.DISPONIBLE || estadoNuevo == EstadoDisponibilidad.INACTIVA)) {
            throw new IllegalArgumentException(
                    "No se puede cambiar el estado a disponible o inactiva porque la propiedad tiene un contrato activo"
            );
        }

        propiedadActual.setDireccion(datosNuevos.getDireccion());
        propiedadActual.setCiudad(datosNuevos.getCiudad());
        propiedadActual.setTipoPropiedad(datosNuevos.getTipoPropiedad());
        propiedadActual.setCantidadAmbientes(datosNuevos.getCantidadAmbientes());
        propiedadActual.setMetrosCuadrados(datosNuevos.getMetrosCuadrados());
        propiedadActual.setDescripcion(datosNuevos.getDescripcion());
        propiedadActual.setPropietario(datosNuevos.getPropietario());
        propiedadActual.setEstadoDisponibilidad(estadoNuevo);

        Propiedad propiedadGuardada = propiedadRepositorio.save(propiedadActual);

        if (estadoAnterior != estadoNuevo) {
            historialEstadoPropiedadRepositorio.save(
                    new HistorialEstadoPropiedad(propiedadGuardada, estadoAnterior, estadoNuevo)
            );
        }

        return propiedadGuardada;
    }

    @Transactional
    public void eliminar(Long id) {
        Propiedad propiedad = buscarPorId(id);

        boolean tieneContratoActivo = contratoRepositorio
                .findByPropiedadAndEstadoContratoAndEliminadoFalse(propiedad, EstadoContrato.ACTIVO)
                .isPresent();

        if (tieneContratoActivo) {
            throw new IllegalArgumentException("No se puede eliminar una propiedad con contrato activo vigente");
        }

        propiedad.setEliminado(true);
        propiedadRepositorio.save(propiedad);
    }

    private void validarDuplicado(Propiedad propiedad) {
        Optional<Propiedad> existente = propiedadRepositorio
                .findByDireccionIgnoreCaseAndCiudadIgnoreCaseAndEliminadoFalse(
                        propiedad.getDireccion(),
                        propiedad.getCiudad()
                );

        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe una propiedad activa con esa dirección y ciudad");
        }
    }
}