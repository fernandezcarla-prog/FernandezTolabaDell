package com.desi.fernandeztolabadell.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.desi.fernandeztolabadell.modelo.Persona;
import com.desi.fernandeztolabadell.repositorio.PersonaRepositorio;

@Component
public class PersonaConverter implements Converter<String, Persona> {

    private final PersonaRepositorio personaRepositorio;

    public PersonaConverter(PersonaRepositorio personaRepositorio) {
        this.personaRepositorio = personaRepositorio;
    }

    @Override
    public Persona convert(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        return personaRepositorio.findById(Long.valueOf(id))
                .orElse(null);
    }
}
