package com.tolaba.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tolaba.modelo.HistorialEstadoContrato;

public interface HistorialEstadoContratoRepositorio extends JpaRepository<HistorialEstadoContrato, Long> {
}