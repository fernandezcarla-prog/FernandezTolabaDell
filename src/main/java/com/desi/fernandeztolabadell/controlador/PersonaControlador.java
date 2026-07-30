package com.desi.fernandeztolabadell.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.desi.fernandeztolabadell.modelo.Persona;
import com.desi.fernandeztolabadell.servicio.PersonaServicio;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/personas")
public class PersonaControlador {

    private final PersonaServicio personaServicio;

    public PersonaControlador(PersonaServicio personaServicio) {
        this.personaServicio = personaServicio;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("personas", personaServicio.listarNoEliminadas());
        return "personas/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("persona", new Persona());
        return "personas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Persona persona,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "personas/formulario";
        }

        personaServicio.guardar(persona);
        redirectAttributes.addFlashAttribute("mensaje", "Persona guardada correctamente");
        return "redirect:/personas";
    }
}