package com.desi.fernandeztolabadell.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.desi.fernandeztolabadell.enums.EstadoContrato;
import com.desi.fernandeztolabadell.enums.EstadoDisponibilidad;
import com.desi.fernandeztolabadell.modelo.Contrato;
import com.desi.fernandeztolabadell.modelo.HistorialEstadoContrato;
import com.desi.fernandeztolabadell.modelo.Propiedad;
import com.desi.fernandeztolabadell.repositorio.ContratoRepositorio;
import com.desi.fernandeztolabadell.repositorio.HistorialEstadoContratoRepositorio;
import com.desi.fernandeztolabadell.repositorio.PropiedadRepositorio;

@Service
public class ContratoServicio {

    private final ContratoRepositorio contratoRepositorio;
    private final HistorialEstadoContratoRepositorio historialEstadoContratoRepositorio;
    private final PropiedadRepositorio propiedadRepositorio;

    public ContratoServicio(
            ContratoRepositorio contratoRepositorio,
            HistorialEstadoContratoRepositorio historialEstadoContratoRepositorio,
            PropiedadRepositorio propiedadRepositorio) {
        this.contratoRepositorio = contratoRepositorio;
        this.historialEstadoContratoRepositorio = historialEstadoContratoRepositorio;
        this.propiedadRepositorio = propiedadRepositorio;
    }

    public List<Contrato> listarNoEliminados() {
        return contratoRepositorio.findByEliminadoFalse();
    }

    public List<Contrato> listarFiltrados(
            Long propiedadId,
            Long inquilinoId,
            EstadoContrato estadoContrato,
            LocalDate fechaInicio) {

        return contratoRepositorio.findByEliminadoFalse()
                .stream()
                .filter(contrato -> propiedadId == null
                        || contrato.getPropiedad().getId().equals(propiedadId))
                .filter(contrato -> inquilinoId == null
                        || contrato.getInquilino().getId().equals(inquilinoId))
                .filter(contrato -> estadoContrato == null
                        || contrato.getEstadoContrato() == estadoContrato)
                .filter(contrato -> fechaInicio == null
                        || contrato.getFechaInicio().equals(fechaInicio))
                .toList();
    }

    public Contrato buscarPorId(Long id) {
        return contratoRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado"));
    }

    public Contrato guardar(Contrato contrato) {
        if (contrato.getEstadoContrato() == null) {
            contrato.setEstadoContrato(EstadoContrato.BORRADOR);
        }

        if (contrato.getEliminado() == null) {
            contrato.setEliminado(false);
        }

        validarContrato(contrato, null);

        Contrato guardado = contratoRepositorio.save(contrato);
        registrarCambioEstado(guardado, null, guardado.getEstadoContrato());

        actualizarEstadoPropiedadSiCorresponde(guardado);

        return guardado;
    }

    public Contrato actualizar(Long id, Contrato datosActualizados) {
        Contrato contrato = buscarPorId(id);

        EstadoContrato estadoAnterior = contrato.getEstadoContrato();

        contrato.setPropiedad(datosActualizados.getPropiedad());
        contrato.setInquilino(datosActualizados.getInquilino());
        contrato.setFechaInicio(datosActualizados.getFechaInicio());
        contrato.setDuracionMeses(datosActualizados.getDuracionMeses());
        contrato.setImporteMensual(datosActualizados.getImporteMensual());
        contrato.setDiaVencimientoMensual(datosActualizados.getDiaVencimientoMensual());
        contrato.setDescripcion(datosActualizados.getDescripcion());
        contrato.setEstadoContrato(datosActualizados.getEstadoContrato());

        validarContrato(contrato, id);

        Contrato guardado = contratoRepositorio.save(contrato);

        if (estadoAnterior != guardado.getEstadoContrato()) {
            registrarCambioEstado(guardado, estadoAnterior, guardado.getEstadoContrato());
        }

        actualizarEstadoPropiedadSiCorresponde(guardado);

        return guardado;
    }

    public void eliminar(Long id) {
        Contrato contrato = buscarPorId(id);

        if (contrato.getEstadoContrato() == EstadoContrato.ACTIVO) {
            throw new IllegalArgumentException("No se puede eliminar un contrato activo");
        }

        contrato.setEliminado(true);
        contratoRepositorio.save(contrato);
    }

    private void validarContrato(Contrato contrato, Long idActual) {
        if (contrato.getPropiedad() == null) {
            throw new IllegalArgumentException("Debe seleccionar una propiedad");
        }

        if (contrato.getInquilino() == null) {
            throw new IllegalArgumentException("Debe seleccionar un inquilino");
        }

        if (contrato.getEstadoContrato() == EstadoContrato.ACTIVO) {
            validarContratoActivo(contrato, idActual);
        }
    }

    private void validarContratoActivo(Contrato contrato, Long idActual) {
        Propiedad propiedad = contrato.getPropiedad();

        if (Boolean.TRUE.equals(propiedad.getEliminado())) {
            throw new IllegalArgumentException("No se puede crear un contrato activo sobre una propiedad eliminada");
        }

        boolean existeContratoActivo;

        if (idActual == null) {
            existeContratoActivo = contratoRepositorio
                    .existsByPropiedadAndEstadoContratoAndEliminadoFalse(
                            propiedad,
                            EstadoContrato.ACTIVO);
        } else {
            existeContratoActivo = contratoRepositorio
                    .existsByPropiedadAndEstadoContratoAndEliminadoFalseAndIdNot(
                            propiedad,
                            EstadoContrato.ACTIVO,
                            idActual);
        }

        if (existeContratoActivo) {
            throw new IllegalArgumentException("Ya existe un contrato activo para esa propiedad");
        }
    }

    private void actualizarEstadoPropiedadSiCorresponde(Contrato contrato) {
        Propiedad propiedad = contrato.getPropiedad();

        if (propiedad == null) {
            return;
        }

        if (contrato.getEstadoContrato() == EstadoContrato.ACTIVO) {
            propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.ALQUILADA);
            propiedadRepositorio.save(propiedad);
        }
    }

    private void registrarCambioEstado(
            Contrato contrato,
            EstadoContrato estadoAnterior,
            EstadoContrato estadoNuevo) {

        HistorialEstadoContrato historial = new HistorialEstadoContrato();
        historial.setContrato(contrato);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setFechaCambio(LocalDateTime.now());

        historialEstadoContratoRepositorio.save(historial);
    }
}