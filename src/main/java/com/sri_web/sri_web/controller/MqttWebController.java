package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiClient;
import com.sri_web.sri_web.dto.MqttCredentialsRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class MqttWebController extends BaseWebController {

    private final SriApiClient apiClient;

    public MqttWebController(SriApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/mqtt")
    public String mqtt(Model model, HttpSession session) {
        page(model, "mqtt", "MQTT", "Conexion con el broker IoT");
        model.addAttribute("mqtt", apiClient.get("/api/mqtt", Map.class, session));
        return "mqtt";
    }

    @PostMapping("/mqtt/connect")
    public String connect(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            apiClient.post("/api/mqtt/connect", new MqttCredentialsRequest(username, password), Map.class, session);
            redirectAttributes.addFlashAttribute("mensaje", "Conexion MQTT establecida.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "No se pudo conectar a MQTT.");
        }
        return "redirect:/mqtt";
    }

    @PostMapping("/mqtt/disconnect")
    public String disconnect(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            apiClient.post("/api/mqtt/disconnect", Map.of(), Map.class, session);
            redirectAttributes.addFlashAttribute("mensaje", "Conexion MQTT cerrada.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "No se pudo desconectar MQTT.");
        }
        return "redirect:/mqtt";
    }
}
