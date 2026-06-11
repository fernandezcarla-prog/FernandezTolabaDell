package com.desi.fernandeztolabadell.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.desi.fernandeztolabadell.enums.EstadoContrato;
import com.desi.fernandeztolabadell.modelo.Contrato;
import com.desi.fernandeztolabadell.servicio.ContratoServicio;
import com.desi.fernandeztolabadell.servicio.PersonaServicio;
import com.desi.fernandeztolabadell.servicio.PropiedadServicio;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/contratos")
public class ContratoControlador {

    private final ContratoServicio contratoServicio;
    private final PropiedadServicio propiedadServicio;
    private final PersonaServicio personaServicio;

    public ContratoControlador(
            ContratoServicio contratoServicio,
            PropiedadServicio propiedadServicio,
            PersonaServicio personaServicio) {
        this.contratoServicio = contratoServicio;
        this.propiedadServicio = propiedadServicio;
        this.personaServicio = personaServicio;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("contratos", contratoServicio.listarNoEliminados());
        return "contratos/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("contrato", new Contrato());
        cargarDatosFormulario(model);
        return "contratos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Contrato contrato,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            cargarDatosFormulario(model);
            return "contratos/formulario";
        }

        try {
            contratoServicio.guardar(contrato);
            redirectAttributes.addFlashAttribute("mensaje", "Contrato guardado correctamente");
            return "redirect:/contratos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return "contratos/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("contrato", contratoServicio.buscarPorId(id));
        cargarDatosFormulario(model);
        return "contratos/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid Contrato contrato,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            cargarDatosFormulario(model);
            return "contratos/formulario";
        }

        try {
            contratoServicio.actualizar(id, contrato);
            redirectAttributes.addFlashAttribute("mensaje", "Contrato actualizado correctamente");
            return "redirect:/contratos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return "contratos/formulario";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contratoServicio.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Contrato eliminado correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/contratos";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("propiedades", propiedadServicio.listarNoEliminadas());
        model.addAttribute("personas", personaServicio.listarNoEliminadas());
        model.addAttribute("estadosContrato", EstadoContrato.values());
    }
}