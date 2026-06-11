package com.desi.fernandeztolabadell.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.desi.fernandeztolabadell.modelo.Propiedad;
import com.desi.fernandeztolabadell.repositorio.PropiedadRepositorio;

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