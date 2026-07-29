package com.tolaba.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.tolaba.modelo.Propiedad;
import com.tolaba.repositorio.PropiedadRepositorio;

@Component
public class PropiedadConverter implements Converter<String, Propiedad> {

    private final PropiedadRepositorio propiedadRepositorio;

    public PropiedadConverter(PropiedadRepositorio propiedadRepositorio) {
        this.propiedadRepositorio = propiedadRepositorio;
    }

    @Override
    public Propiedad convert(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        return propiedadRepositorio.findById(Long.valueOf(id))
                .orElse(null);
    }
}