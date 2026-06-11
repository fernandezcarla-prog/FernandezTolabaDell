package com.desi.fernandeztolabadell.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desi.fernandeztolabadell.enums.EstadoContrato;
import com.desi.fernandeztolabadell.enums.EstadoDisponibilidad;
import com.desi.fernandeztolabadell.modelo.Contrato;
import com.desi.fernandeztolabadell.modelo.HistorialEstadoContrato;
import com.desi.fernandeztolabadell.modelo.HistorialEstadoPropiedad;
import com.desi.fernandeztolabadell.modelo.Propiedad;
import com.desi.fernandeztolabadell.repositorio.ContratoRepositorio;
import com.desi.fernandeztolabadell.repositorio.HistorialEstadoContratoRepositorio;
import com.desi.fernandeztolabadell.repositorio.HistorialEstadoPropiedadRepositorio;
import com.desi.fernandeztolabadell.repositorio.PropiedadRepositorio;

@Service
public class ContratoServicio {

    private final ContratoRepositorio contratoRepositorio;
    private final PropiedadRepositorio propiedadRepositorio;
    private final HistorialEstadoContratoRepositorio historialEstadoContratoRepositorio;
    private final HistorialEstadoPropiedadRepositorio historialEstadoPropiedadRepositorio;

    public ContratoServicio(
            ContratoRepositorio contratoRepositorio,
            PropiedadRepositorio propiedadRepositorio,
            HistorialEstadoContratoRepositorio historialEstadoContratoRepositorio,
            HistorialEstadoPropiedadRepositorio historialEstadoPropiedadRepositorio) {
        this.contratoRepositorio = contratoRepositorio;
        this.propiedadRepositorio = propiedadRepositorio;
        this.historialEstadoContratoRepositorio = historialEstadoContratoRepositorio;
        this.historialEstadoPropiedadRepositorio = historialEstadoPropiedadRepositorio;
    }

    public List<Contrato> listarNoEliminados() {
        return contratoRepositorio.findByEliminadoFalse();
    }

    public Contrato buscarPorId(Long id) {
        return contratoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el contrato indicado"));
    }

    @Transactional
    public Contrato crear(Contrato contrato) {
        Propiedad propiedad = propiedadRepositorio.findById(contrato.getPropiedad().getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe la propiedad indicada"));

        if (contrato.getEstadoContrato() == null) {
            contrato.setEstadoContrato(EstadoContrato.BORRADOR);
        }

        if (contrato.getEstadoContrato() == EstadoContrato.ACTIVO) {
            validarActivacionContrato(propiedad, null);
            cambiarEstadoPropiedad(propiedad, EstadoDisponibilidad.ALQUILADA);
        }

        contrato.setPropiedad(propiedad);
        contrato.setEliminado(false);

        Contrato contratoGuardado = contratoRepositorio.save(contrato);

        historialEstadoContratoRepositorio.save(
                new HistorialEstadoContrato(
                        contratoGuardado,
                        null,
                        contratoGuardado.getEstadoContrato()
                )
        );

        return contratoGuardado;
    }

    @Transactional
    public Contrato modificar(Long id, Contrato datosNuevos) {
        Contrato contratoActual = buscarPorId(id);

        EstadoContrato estadoAnterior = contratoActual.getEstadoContrato();
        EstadoContrato estadoNuevo = datosNuevos.getEstadoContrato();

        validarTransicionEstado(estadoAnterior, estadoNuevo);

        Propiedad propiedad = contratoActual.getPropiedad();

        if (estadoNuevo == EstadoContrato.ACTIVO && estadoAnterior != EstadoContrato.ACTIVO) {
            validarActivacionContrato(propiedad, id);
            cambiarEstadoPropiedad(propiedad, EstadoDisponibilidad.ALQUILADA);
        }

        if ((estadoNuevo == EstadoContrato.FINALIZADO || estadoNuevo == EstadoContrato.RESCINDIDO)
                && estadoAnterior == EstadoContrato.ACTIVO) {
            cambiarEstadoPropiedad(propiedad, EstadoDisponibilidad.DISPONIBLE);
        }

        contratoActual.setInquilino(datosNuevos.getInquilino());
        contratoActual.setFechaInicio(datosNuevos.getFechaInicio());
        contratoActual.setDuracionMeses(datosNuevos.getDuracionMeses());
        contratoActual.setImporteMensual(datosNuevos.getImporteMensual());
        contratoActual.setDiaVencimientoMensual(datosNuevos.getDiaVencimientoMensual());
        contratoActual.setDescripcion(datosNuevos.getDescripcion());
        contratoActual.setEstadoContrato(estadoNuevo);

        Contrato contratoGuardado = contratoRepositorio.save(contratoActual);

        if (estadoAnterior != estadoNuevo) {
            historialEstadoContratoRepositorio.save(
                    new HistorialEstadoContrato(contratoGuardado, estadoAnterior, estadoNuevo)
            );
        }

        return contratoGuardado;
    }

    @Transactional
    public void eliminar(Long id) {
        Contrato contrato = buscarPorId(id);

        if (contrato.getEstadoContrato() != EstadoContrato.BORRADOR) {
            throw new IllegalArgumentException("Solo pueden eliminarse contratos en estado borrador");
        }

        contrato.setEliminado(true);
        contratoRepositorio.save(contrato);
    }

    private void validarActivacionContrato(Propiedad propiedad, Long idContratoActual) {
        if (Boolean.TRUE.equals(propiedad.getEliminado())) {
            throw new IllegalArgumentException("No se puede activar un contrato sobre una propiedad eliminada");
        }

        if (propiedad.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
            throw new IllegalArgumentException("No se puede activar el contrato porque la propiedad no está disponible");
        }

        Optional<Contrato> contratoActivo = contratoRepositorio
                .findByPropiedadAndEstadoContratoAndEliminadoFalse(propiedad, EstadoContrato.ACTIVO);

        if (contratoActivo.isPresent()
                && (idContratoActual == null || !contratoActivo.get().getId().equals(idContratoActual))) {
            throw new IllegalArgumentException("La propiedad ya tiene un contrato activo");
        }
    }

    private void validarTransicionEstado(EstadoContrato estadoAnterior, EstadoContrato estadoNuevo) {
        if ((estadoAnterior == EstadoContrato.FINALIZADO || estadoAnterior == EstadoContrato.RESCINDIDO)
                && estadoNuevo == EstadoContrato.ACTIVO) {
            throw new IllegalArgumentException("No se puede volver de finalizado o rescindido a activo");
        }

        boolean transicionValida =
                estadoAnterior == estadoNuevo
                        || (estadoAnterior == EstadoContrato.BORRADOR && estadoNuevo == EstadoContrato.ACTIVO)
                        || (estadoAnterior == EstadoContrato.ACTIVO && estadoNuevo == EstadoContrato.FINALIZADO)
                        || (estadoAnterior == EstadoContrato.ACTIVO && estadoNuevo == EstadoContrato.RESCINDIDO);

        if (!transicionValida) {
            throw new IllegalArgumentException("Cambio de estado de contrato no permitido");
        }
    }

    private void cambiarEstadoPropiedad(Propiedad propiedad, EstadoDisponibilidad nuevoEstado) {
        EstadoDisponibilidad estadoAnterior = propiedad.getEstadoDisponibilidad();

        if (estadoAnterior != nuevoEstado) {
            propiedad.setEstadoDisponibilidad(nuevoEstado);
            Propiedad propiedadGuardada = propiedadRepositorio.save(propiedad);

            historialEstadoPropiedadRepositorio.save(
                    new HistorialEstadoPropiedad(propiedadGuardada, estadoAnterior, nuevoEstado)
            );
        }
    }
}