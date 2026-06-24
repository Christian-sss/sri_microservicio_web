package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiClient;
import com.sri_web.sri_web.dto.CultivoRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class CultivosWebController extends BaseWebController {

    private final SriApiClient apiClient;

    public CultivosWebController(SriApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/cultivos")
    public String cultivos(Model model, HttpSession session) {
        page(model, "cultivos", "Cultivos", "Gestion de perfiles disponibles para riego");
        model.addAttribute("cultivos", apiClient.get("/api/cultivos", new ParameterizedTypeReference<List<Map<String, Object>>>() {}, session));
        return "cultivos";
    }

    @PostMapping("/cultivos")
    public String crear(@RequestParam String nombre,
                        @RequestParam Integer humedadMinOptima,
                        @RequestParam Integer humedadMaxOptima,
                        @RequestParam Integer duracionRiegoMinutos,
                        @RequestParam(required = false) String tratoRecomendado,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            apiClient.post("/api/cultivos", new CultivoRequest(nombre, humedadMinOptima, humedadMaxOptima, duracionRiegoMinutos, tratoRecomendado), Map.class, session);
            redirectAttributes.addFlashAttribute("mensaje", "Cultivo guardado correctamente.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "No se pudo guardar el cultivo.");
        }
        return "redirect:/cultivos";
    }

    @PostMapping("/cultivos/{id}/toggle")
    public String toggle(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            apiClient.put("/api/cultivos/" + id + "/toggle-estado", Map.of(), Void.class, session);
            redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cambiar el estado.");
        }
        return "redirect:/cultivos";
    }
}
