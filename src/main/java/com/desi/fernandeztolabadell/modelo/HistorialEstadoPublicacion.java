package com.desi.fernandeztolabadell.modelo;

import java.time.LocalDateTime;

import com.desi.fernandeztolabadell.enums.EstadoPublicacion;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "historial_estados_publicacion")
public class HistorialEstadoPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Publicacion publicacion;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estadoNuevo;

    private LocalDateTime fechaCambio;

    public HistorialEstadoPublicacion() {
    }

    public HistorialEstadoPublicacion(Publicacion publicacion, EstadoPublicacion estadoAnterior,
            EstadoPublicacion estadoNuevo) {
        this.publicacion = publicacion;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public EstadoPublicacion getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoPublicacion estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoPublicacion getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoPublicacion estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}