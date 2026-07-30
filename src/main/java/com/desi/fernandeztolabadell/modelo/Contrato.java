package com.desi.fernandeztolabadell.modelo;

import java.time.LocalDate;

import com.desi.fernandeztolabadell.enums.EstadoContrato;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La propiedad es obligatoria")
    @ManyToOne
    private Propiedad propiedad;

    @NotNull(message = "El inquilino es obligatorio")
    @ManyToOne
    private Persona inquilino;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La duración en meses es obligatoria")
    @Positive(message = "La duración en meses debe ser un número positivo")
    private Integer duracionMeses;

    @NotNull(message = "El importe mensual es obligatorio")
    @Positive(message = "El importe mensual debe ser un número positivo")
    private Double importeMensual;

    @NotNull(message = "El día de vencimiento mensual es obligatorio")
    @Min(value = 1, message = "El día de vencimiento debe estar entre 1 y 31")
    @Max(value = 31, message = "El día de vencimiento debe estar entre 1 y 31")
    private Integer diaVencimientoMensual;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El estado del contrato es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoContrato estadoContrato = EstadoContrato.BORRADOR;

    private Boolean eliminado = false;

    public Contrato() {
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

    public Persona getInquilino() {
        return inquilino;
    }

    public void setInquilino(Persona inquilino) {
        this.inquilino = inquilino;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Integer getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(Integer duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public Double getImporteMensual() {
        return importeMensual;
    }

    public void setImporteMensual(Double importeMensual) {
        this.importeMensual = importeMensual;
    }

    public Integer getDiaVencimientoMensual() {
        return diaVencimientoMensual;
    }

    public void setDiaVencimientoMensual(Integer diaVencimientoMensual) {
        this.diaVencimientoMensual = diaVencimientoMensual;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public EstadoContrato getEstadoContrato() {
        return estadoContrato;
    }

    public void setEstadoContrato(EstadoContrato estadoContrato) {
        this.estadoContrato = estadoContrato;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
}