package com.tolaba.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tolaba.modelo.Persona;

public interface PersonaRepositorio extends JpaRepository<Persona, Long> {

    List<Persona> findByEliminadoFalse();

}