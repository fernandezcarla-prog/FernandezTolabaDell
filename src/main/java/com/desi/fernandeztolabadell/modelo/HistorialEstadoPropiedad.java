package com.desi.fernandeztolabadell.modelo;

import java.time.LocalDateTime;

import com.desi.fernandeztolabadell.enums.EstadoDisponibilidad;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "historial_estados_propiedad")
public class HistorialEstadoPropiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Propiedad propiedad;

    @Enumerated(EnumType.STRING)
    private EstadoDisponibilidad estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoDisponibilidad estadoNuevo;

    private LocalDateTime fechaCambio;

    public HistorialEstadoPropiedad() {
    }

    public HistorialEstadoPropiedad(Propiedad propiedad, EstadoDisponibilidad estadoAnterior,
            EstadoDisponibilidad estadoNuevo) {
        this.propiedad = propiedad;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }

    public EstadoDisponibilidad getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoDisponibilidad estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoDisponibilidad getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoDisponibilidad estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}