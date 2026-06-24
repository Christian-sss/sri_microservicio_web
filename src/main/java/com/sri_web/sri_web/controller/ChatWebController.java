package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiClient;
import com.sri_web.sri_web.dto.ChatRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class ChatWebController extends BaseWebController {

    private final SriApiClient apiClient;

    public ChatWebController(SriApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/chat")
    public String chat(Model model) {
        page(model, "chat", "Asistente", "Consulta rapida sobre riego y cultivo");
        return "chat";
    }

    @PostMapping("/chat")
    public String enviar(@RequestParam String mensaje, Model model, HttpSession session) {
        page(model, "chat", "Asistente", "Consulta rapida sobre riego y cultivo");
        model.addAttribute("pregunta", mensaje);
        try {
            Map<?, ?> response = apiClient.post("/api/chat", new ChatRequest(mensaje), Map.class, session);
            model.addAttribute("respuesta", response.get("respuesta"));
        } catch (Exception exception) {
            model.addAttribute("error", "No se pudo obtener respuesta del asistente.");
        }
        return "chat";
    }
}
