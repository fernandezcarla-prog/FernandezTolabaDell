package com.desi.fernandeztolabadell.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.desi.fernandeztolabadell.modelo.Persona;

public interface PersonaRepositorio extends JpaRepository<Persona, Long> {

    List<Persona> findByEliminadoFalse();

}