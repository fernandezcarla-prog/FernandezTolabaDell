package com.tolaba.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tolaba.enums.EstadoContrato;
import com.tolaba.enums.EstadoDisponibilidad;
import com.tolaba.modelo.Contrato;
import com.tolaba.modelo.HistorialEstadoContrato;
import com.tolaba.modelo.Propiedad;
import com.tolaba.repositorio.ContratoRepositorio;
import com.tolaba.repositorio.HistorialEstadoContratoRepositorio;
import com.tolaba.repositorio.PropiedadRepositorio;

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

    @Transactional(readOnly = true)
    public List<Contrato> listarNoEliminados() {
        return contratoRepositorio.findByEliminadoFalse();
    }

    @Transactional(readOnly = true)
    public List<Contrato> listarFiltrados(Long propiedadId, Long inquilinoId, EstadoContrato estadoContrato, LocalDate fechaInicio) {
        return contratoRepositorio.findByEliminadoFalse().stream()
                .filter(c -> propiedadId == null || (c.getPropiedad() != null && c.getPropiedad().getId().equals(propiedadId)))
                .filter(c -> inquilinoId == null || (c.getInquilino() != null && c.getInquilino().getId().equals(inquilinoId)))
                .filter(c -> estadoContrato == null || c.getEstadoContrato() == estadoContrato)
                .filter(c -> fechaInicio == null || (c.getFechaInicio() != null && c.getFechaInicio().equals(fechaInicio)))
                .toList();
    }

    @Transactional(readOnly = true)
    public Contrato buscarPorId(Long id) {
        return contratoRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Error Operativo InmoGesta: No se localizó ningún registro contractual activo bajo el ID: " + id));
    }

    @Transactional
    public Contrato guardar(Contrato contrato) {
        if (contrato.getEstadoContrato() == null) contrato.setEstadoContrato(EstadoContrato.BORRADOR);
        if (contrato.getEliminado() == null) contrato.setEliminado(false);

        validarEstructuraContrato(contrato, null);

        Contrato contratoGuardado = contratoRepositorio.save(contrato);
        registrarTransicionEnHistorial(contratoGuardado, null, contratoGuardado.getEstadoContrato());
        sincronizarDisponibilidadInmueble(contratoGuardado);

        return contratoGuardado;
    }

    @Transactional
    public Contrato actualizar(Long id, Contrato datosNuevos) {
        Contrato contratoExistente = buscarPorId(id);
        EstadoContrato estadoPrevio = contratoExistente.getEstadoContrato();

        validarMatrizTransicionEstados(estadoPrevio, datosNuevos.getEstadoContrato());

        contratoExistente.setPropiedad(datosNuevos.getPropiedad());
        contratoExistente.setInquilino(datosNuevos.getInquilino());
        contratoExistente.setFechaInicio(datosNuevos.getFechaInicio());
        contratoExistente.setDuracionMeses(datosNuevos.getDuracionMeses());
        contratoExistente.setImporteMensual(datosNuevos.getImporteMensual());
        contratoExistente.setDiaVencimientoMensual(datosNuevos.getDiaVencimientoMensual());
        contratoExistente.setDescripcion(datosNuevos.getDescripcion());
        contratoExistente.setEstadoContrato(datosNuevos.getEstadoContrato());

        validarEstructuraContrato(contratoExistente, id);

        Contrato contratoActualizado = contratoRepositorio.save(contratoExistente);

        if (estadoPrevio != contratoActualizado.getEstadoContrato()) {
            registrarTransicionEnHistorial(contratoActualizado, estadoPrevio, contratoActualizado.getEstadoContrato());
        }

        sincronizarDisponibilidadInmueble(contratoActualizado);
        return contratoActualizado;
    }

    @Transactional
    public void eliminar(Long id) {
        Contrato contrato = buscarPorId(id);

        if (contrato.getEstadoContrato() != EstadoContrato.BORRADOR) {
            throw new IllegalArgumentException("Restricción de Auditoría InmoGesta: La eliminación lógica en base de datos se limita a contratos en estado BORRADOR.");
        }

        contrato.setEliminado(true);
        contratoRepositorio.save(contrato);
    }

    private void validarEstructuraContrato(Contrato contrato, Long idActual) {
        if (contrato.getPropiedad() == null) throw new IllegalArgumentException("Error de Estructura: La asignación de una propiedad inmueble es requerida.");
        if (contrato.getInquilino() == null) throw new IllegalArgumentException("Error de Estructura: La vinculación de un inquilino es requerida.");

        if (contrato.getEstadoContrato() == EstadoContrato.ACTIVO) {
            validarDisponibilidadExclusiva(contrato, idActual);
        }
    }

    private void validarDisponibilidadExclusiva(Contrato contrato, Long idActual) {
        Propiedad propiedad = contrato.getPropiedad();

        if (Boolean.TRUE.equals(propiedad.getEliminado())) {
            throw new IllegalArgumentException("Conflicto Comercial: No se permite operar sobre propiedades dadas de baja en el inventario.");
        }

        boolean conflictoActivo = (idActual == null)
                ? contratoRepositorio.existsByPropiedadAndEstadoContratoAndEliminadoFalse(propiedad, EstadoContrato.ACTIVO)
                : contratoRepositorio.existsByPropiedadAndEstadoContratoAndEliminadoFalseAndIdNot(propiedad, EstadoContrato.ACTIVO, idActual);

        if (conflictoActivo) {
            throw new IllegalArgumentException("Inconsistencia: El inmueble seleccionado ya posee un contrato de alquiler ACTIVO vigente en el sistema.");
        }
    }

    private void validarMatrizTransicionEstados(EstadoContrato origen, EstadoContrato destino) {
        if (origen == destino) return;

        if ((origen == EstadoContrato.FINALIZADO || origen == EstadoContrato.RESCINDIDO) && destino == EstadoContrato.ACTIVO) {
            throw new IllegalArgumentException("Violación de Ciclo de Vida: Un acuerdo legal en estado Finalizado o Rescindido no puede ser reactivado.");
        }
    }

    private void sincronizarDisponibilidadInmueble(Contrato contrato) {
        Propiedad propiedad = contrato.getPropiedad();
        if (propiedad == null) return;

        if (contrato.getEstadoContrato() == EstadoContrato.ACTIVO) {
            propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.ALQUILADA);
            propiedadRepositorio.save(propiedad);
        } else if (contrato.getEstadoContrato() == EstadoContrato.FINALIZADO || contrato.getEstadoContrato() == EstadoContrato.RESCINDIDO) {
            propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
            propiedadRepositorio.save(propiedad);
        }
    }

    private void registrarTransicionEnHistorial(Contrato contrato, EstadoContrato anterior, EstadoContrato nuevo) {
        HistorialEstadoContrato historial = new HistorialEstadoContrato();
        historial.setContrato(contrato);
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(nuevo);
        historial.setFechaCambio(LocalDateTime.now());
        historialEstadoContratoRepositorio.save(historial);
    }
}
