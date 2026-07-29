package com.tolaba.modelo;

import java.time.LocalDate;

import com.tolaba.enums.EstadoContrato;

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

    @NotNull(message = "La propiedad inmueble es requerida de forma obligatoria.")
    @ManyToOne
    private Propiedad propiedad;

    @NotNull(message = "El inquilino es un campo obligatorio para la validez legal del acuerdo.")
    @ManyToOne
    private Persona inquilino;

    @NotNull(message = "La fecha de inicio del contrato es obligatoria.")
    private LocalDate fechaInicio;

    @NotNull(message = "La duración en meses es obligatoria.")
    @Positive(message = "La duración en meses debe estructurarse mediante un número positivo.")
    private Integer duracionMeses;

    @NotNull(message = "El importe mensual es obligatorio.")
    @Positive(message = "El importe mensual debe ser un monto numérico positivo.")
    private Double importeMensual;

    @NotNull(message = "El día de vencimiento mensual es obligatorio.")
    @Min(value = 1, message = "El día de vencimiento locativo debe estar comprendido entre 1 y 31.")
    @Max(value = 31, message = "El día de vencimiento locativo debe estar comprendido entre 1 y 31.")
    private Integer diaVencimientoMensual;

    @NotBlank(message = "La descripción o cuerpo de cláusulas contractuales es obligatoria.")
    private String descripcion;

    @NotNull(message = "El estado del ciclo de vida contractual es requerido.")
    @Enumerated(EnumType.STRING)
    private EstadoContrato estadoContrato = EstadoContrato.BORRADOR;

    private Boolean eliminado = false;

    public Contrato() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
