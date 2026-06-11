package com.desi.fernandeztolabadell.servicio;

import java.util.List;

import org.springframework.stereotype.Service;

import com.desi.fernandeztolabadell.modelo.Persona;
import com.desi.fernandeztolabadell.repositorio.PersonaRepositorio;

@Service
public class PersonaServicio {

    private final PersonaRepositorio personaRepositorio;

    public PersonaServicio(PersonaRepositorio personaRepositorio) {
        this.personaRepositorio = personaRepositorio;
    }

    public List<Persona> listarNoEliminadas() {
        return personaRepositorio.findByEliminadoFalse();
    }

    public Persona buscarPorId(Long id) {
        return personaRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la persona indicada"));
    }

    public Persona guardar(Persona persona) {
        return personaRepositorio.save(persona);
    }
}
