package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardWebController extends BaseWebController {

    private final SriApiClient apiClient;

    public DashboardWebController(SriApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        page(model, "dashboard", "Dashboard", "Estado general del sistema de riego");
        model.addAttribute("dashboard", apiClient.get("/api/dashboard", Map.class, session));
        model.addAttribute("estadoRiego", apiClient.get("/api/riego/estado", Map.class, session));
        return "dashboard";
    }
}
