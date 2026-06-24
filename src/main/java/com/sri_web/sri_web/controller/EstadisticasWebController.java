package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
public class EstadisticasWebController extends BaseWebController {

    private final SriApiClient apiClient;

    public EstadisticasWebController(SriApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/estadisticas")
    public String estadisticas(@RequestParam(required = false) String cultivoId,
                               Model model,
                               HttpSession session) {
        page(model, "estadisticas", "Estadisticas", "Indicadores historicos del sistema");
        model.addAttribute("resumen", apiClient.get(withCultivo("/api/estadisticas/resumen", cultivoId), Map.class, session));
        model.addAttribute("distribucion", apiClient.get(withCultivo("/api/estadisticas/distribucion-modos", cultivoId), Map.class, session));
        model.addAttribute("consumo", apiClient.get("/api/estadisticas/consumo-detalle", List.class, session));
        model.addAttribute("telemetria", apiClient.get(withCultivo("/api/estadisticas/telemetria", cultivoId), List.class, session));
        model.addAttribute("cultivos", apiClient.get("/api/cultivos", new ParameterizedTypeReference<List<Map<String, Object>>>() {}, session));
        model.addAttribute("selectedCultivoId", cultivoId);
        return "estadisticas";
    }

    private String withCultivo(String endpoint, String cultivoId) {
        if (cultivoId == null || cultivoId.isBlank()) return endpoint;
        return endpoint + "?cultivoId=" + URLEncoder.encode(cultivoId, StandardCharsets.UTF_8);
    }
}
