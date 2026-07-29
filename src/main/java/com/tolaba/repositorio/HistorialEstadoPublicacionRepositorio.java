package com.tolaba.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tolaba.modelo.HistorialEstadoPublicacion;

public interface HistorialEstadoPublicacionRepositorio extends JpaRepository<HistorialEstadoPublicacion, Long> {
}