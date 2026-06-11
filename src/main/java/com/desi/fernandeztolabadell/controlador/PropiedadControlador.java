package com.desi.fernandeztolabadell.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.desi.fernandeztolabadell.enums.EstadoDisponibilidad;
import com.desi.fernandeztolabadell.enums.TipoPropiedad;
import com.desi.fernandeztolabadell.modelo.Propiedad;
import com.desi.fernandeztolabadell.servicio.PersonaServicio;
import com.desi.fernandeztolabadell.servicio.PropiedadServicio;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/propiedades")
public class PropiedadControlador {

    private final PropiedadServicio propiedadServicio;
    private final PersonaServicio personaServicio;

    public PropiedadControlador(PropiedadServicio propiedadServicio, PersonaServicio personaServicio) {
        this.propiedadServicio = propiedadServicio;
        this.personaServicio = personaServicio;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("propiedades", propiedadServicio.listarNoEliminadas());
        return "propiedades/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("propiedad", new Propiedad());
        cargarDatosFormulario(model);
        return "propiedades/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Propiedad propiedad,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            cargarDatosFormulario(model);
            return "propiedades/formulario";
        }

        try {
            propiedadServicio.crear(propiedad);
            redirectAttributes.addFlashAttribute("mensaje", "Propiedad creada correctamente");
            return "redirect:/propiedades";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return "propiedades/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("propiedad", propiedadServicio.buscarPorId(id));
            cargarDatosFormulario(model);
            return "propiedades/formulario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/propiedades";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid Propiedad propiedad,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            propiedad.setEliminado(false);
            cargarDatosFormulario(model);
            return "propiedades/formulario";
        }

        try {
            propiedadServicio.modificar(id, propiedad);
            redirectAttributes.addFlashAttribute("mensaje", "Propiedad modificada correctamente");
            return "redirect:/propiedades";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return "propiedades/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            propiedadServicio.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Propiedad eliminada correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/propiedades";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("tiposPropiedad", TipoPropiedad.values());
        model.addAttribute("estadosDisponibilidad", EstadoDisponibilidad.values());
        model.addAttribute("personas", personaServicio.listarNoEliminadas());
    }
}