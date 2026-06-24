package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiClient;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class ReportesWebController extends BaseWebController {

    private final SriApiClient apiClient;

    public ReportesWebController(SriApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/reportes")
    public String reportes(Model model, HttpSession session) {
        page(model, "reportes", "Reportes", "Descarga de reportes del sistema");
        model.addAttribute("cultivos", apiClient.get("/api/cultivos", new ParameterizedTypeReference<List<Map<String, Object>>>() {}, session));
        return "reportes";
    }

    @GetMapping("/reportes/modos")
    public void modos(@RequestParam(required = false) String fechaInicio,
                      @RequestParam(required = false) String fechaFin,
                      @RequestParam(required = false) String cultivoId,
                      HttpSession session,
                      HttpServletResponse response) throws IOException {
        descargarPdf("/api/reportes/descargar-pdf", fechaInicio, fechaFin, cultivoId, "Reporte_Sistema_Riego.pdf", session, response);
    }

    @GetMapping("/reportes/consumo")
    public void consumo(@RequestParam(required = false) String fechaInicio,
                        @RequestParam(required = false) String fechaFin,
                        @RequestParam(required = false) String cultivoId,
                        HttpSession session,
                        HttpServletResponse response) throws IOException {
        descargarPdf("/api/reportes/consumo-agua", fechaInicio, fechaFin, cultivoId, "Reporte_Consumo_Agua.pdf", session, response);
    }

    private void descargarPdf(String endpoint, String fechaInicio, String fechaFin, String cultivoId, String filename,
                              HttpSession session, HttpServletResponse response) throws IOException {
        StringBuilder uri = new StringBuilder(endpoint).append("?");
        if (fechaInicio != null && !fechaInicio.isBlank()) uri.append("fechaInicio=").append(fechaInicio).append("&");
        if (fechaFin != null && !fechaFin.isBlank()) uri.append("fechaFin=").append(fechaFin).append("&");
        if (cultivoId != null && !cultivoId.isBlank()) uri.append("cultivoId=").append(cultivoId).append("&");

        byte[] pdf = apiClient.getBytes(uri.toString(), session);
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.getOutputStream().write(pdf);
    }
}
