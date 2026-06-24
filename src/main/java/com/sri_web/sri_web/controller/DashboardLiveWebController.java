package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardLiveWebController {

    private final SriApiClient apiClient;

    public DashboardLiveWebController(SriApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/web-api/dashboard/live")
    public Map<?, ?> live(HttpSession session) {
        return apiClient.get("/api/dashboard/live", Map.class, session);
    }
}
