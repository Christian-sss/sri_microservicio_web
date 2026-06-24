package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiClient;
import com.sri_web.sri_web.client.SriApiException;
import com.sri_web.sri_web.dto.RiegoManualRequest;
import com.sri_web.sri_web.dto.RiegoModoRequest;
import com.sri_web.sri_web.dto.RiegoPerfilRequest;
import com.sri_web.sri_web.dto.RiegoProgramacionRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class RiegoWebController extends BaseWebController {

    private final SriApiClient apiClient;

    public RiegoWebController(SriApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/riego")
    public String riego(Model model, HttpSession session) {
        page(model, "riego", "Riego", "Control manual y automatico de la bomba");
        model.addAttribute("estado", apiClient.get("/api/riego/estado", Map.class, session));
        model.addAttribute("cultivos", apiClient.get("/api/riego/cultivos-activos", new ParameterizedTypeReference<List<Map<String, Object>>>() {}, session));
        return "riego";
    }

    @PostMapping("/riego/modo")
    public String modo(@RequestParam String modo, HttpSession session, RedirectAttributes redirectAttributes) {
        return ejecutar(() -> apiClient.post("/api/riego/modo", new RiegoModoRequest(modo), Map.class, session), redirectAttributes);
    }

    @PostMapping("/riego/perfil")
    public String perfil(@RequestParam Integer cultivoId, HttpSession session, RedirectAttributes redirectAttributes) {
        return ejecutar(() -> apiClient.post("/api/riego/perfil", new RiegoPerfilRequest(cultivoId), Map.class, session), redirectAttributes);
    }

    @PostMapping("/riego/programacion")
    public String programacion(@RequestParam Integer cultivoId,
                               @RequestParam String horaRiego,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        return ejecutar(() -> apiClient.post("/api/riego/programacion", new RiegoProgramacionRequest(cultivoId, horaRiego), Map.class, session), redirectAttributes);
    }

    @PostMapping("/riego/manual")
    public String manual(@RequestParam String orden,
                         @RequestParam Integer cultivoId,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        return ejecutar(() -> apiClient.post("/api/riego/manual", new RiegoManualRequest(orden, cultivoId), Map.class, session), redirectAttributes);
    }

    private String ejecutar(Runnable action, RedirectAttributes redirectAttributes) {
        try {
            action.run();
            redirectAttributes.addFlashAttribute("mensaje", "Operacion realizada correctamente.");
        } catch (SriApiException exception) {
            redirectAttributes.addFlashAttribute("error", extraerMensajeApi(exception.getMessage()));
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage() != null ? exception.getMessage() : "No se pudo completar la operacion.");
        }
        return "redirect:/riego";
    }

    private String extraerMensajeApi(String body) {
        if (body == null || body.isBlank()) {
            return "No se pudo completar la operacion.";
        }

        int errorIndex = body.indexOf("\"error\"");
        if (errorIndex >= 0) {
            int colon = body.indexOf(':', errorIndex);
            int start = body.indexOf('"', colon + 1);
            int end = body.indexOf('"', start + 1);
            if (start >= 0 && end > start) {
                return body.substring(start + 1, end);
            }
        }

        return body;
    }
}
