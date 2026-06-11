package com.desi.fernandeztolabadell.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.desi.fernandeztolabadell.enums.EstadoPublicacion;
import com.desi.fernandeztolabadell.modelo.Publicacion;
import com.desi.fernandeztolabadell.servicio.PropiedadServicio;
import com.desi.fernandeztolabadell.servicio.PublicacionServicio;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionControlador {

    private final PublicacionServicio publicacionServicio;
    private final PropiedadServicio propiedadServicio;

    public PublicacionControlador(
            PublicacionServicio publicacionServicio,
            PropiedadServicio propiedadServicio) {
        this.publicacionServicio = publicacionServicio;
        this.propiedadServicio = propiedadServicio;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) Long propiedadId,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) EstadoPublicacion estado,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            Model model) {

        model.addAttribute("publicaciones",
                publicacionServicio.filtrar(propiedadId, ciudad, estado, precioMin, precioMax));

        model.addAttribute("propiedades", propiedadServicio.listarNoEliminadas());
        model.addAttribute("estadosPublicacion", EstadoPublicacion.values());

        model.addAttribute("propiedadId", propiedadId);
        model.addAttribute("ciudad", ciudad);
        model.addAttribute("estado", estado);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);

        return "publicaciones/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("publicacion", new Publicacion());
        cargarDatosFormulario(model);
        return "publicaciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Publicacion publicacion,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            cargarDatosFormulario(model);
            return "publicaciones/formulario";
        }

        try {
            publicacionServicio.guardar(publicacion);
            redirectAttributes.addFlashAttribute("mensaje", "Publicación guardada correctamente");
            return "redirect:/publicaciones";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return "publicaciones/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("publicacion", publicacionServicio.buscarPorId(id));
        cargarDatosFormulario(model);
        return "publicaciones/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid Publicacion publicacion,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            cargarDatosFormulario(model);
            return "publicaciones/formulario";
        }

        try {
            publicacionServicio.actualizar(id, publicacion);
            redirectAttributes.addFlashAttribute("mensaje", "Publicación actualizada correctamente");
            return "redirect:/publicaciones";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return "publicaciones/formulario";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            publicacionServicio.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Publicación eliminada correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/publicaciones";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("propiedades", propiedadServicio.listarNoEliminadas());
        model.addAttribute("estadosPublicacion", EstadoPublicacion.values());
    }
}