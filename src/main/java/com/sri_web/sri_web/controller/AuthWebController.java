package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiClient;
import com.sri_web.sri_web.client.SriApiException;
import com.sri_web.sri_web.dto.AuthRequest;
import com.sri_web.sri_web.service.CaptchaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class AuthWebController extends BaseWebController {

    private final SriApiClient apiClient;
    private final CaptchaService captchaService;

    public AuthWebController(SriApiClient apiClient, CaptchaService captchaService) {
        this.apiClient = apiClient;
        this.captchaService = captchaService;
    }

    @GetMapping({"/", "/login"})
    public String login(Model model, HttpSession session) {
        addCaptcha(model, session);
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model, HttpSession session) {
        addCaptcha(model, session);
        return "registro";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam String captcha,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (!captchaService.isValid(captcha, session)) {
            redirectAttributes.addFlashAttribute("error", "Captcha incorrecto. Intenta nuevamente.");
            redirectAttributes.addFlashAttribute("username", email);
            return "redirect:/login";
        }

        try {
            Map<?, ?> response = apiClient.post("/api/auth/login", new AuthRequest(email, password), Map.class, session);
            session.setAttribute("usuario", response.get("usuario"));
            return "redirect:/dashboard";
        } catch (SriApiException exception) {
            redirectAttributes.addFlashAttribute("error", "Usuario o contrasena incorrectos.");
            return "redirect:/login";
        }
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String confirmPassword,
                            @RequestParam String captcha,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!captchaService.isValid(captcha, session)) {
            redirectAttributes.addFlashAttribute("error", "Captcha incorrecto. Intenta nuevamente.");
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/registro";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contrasenas no coinciden.");
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/registro";
        }

        try {
            apiClient.post("/auth/register", new AuthRequest(username, password), Map.class, session);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado correctamente. Inicia sesion.");
            return "redirect:/login";
        } catch (SriApiException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/registro";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        try {
            apiClient.post("/auth/logout", Map.of(), Map.class, session);
        } catch (Exception ignored) {
        }
        apiClient.clearSession(session);
        session.invalidate();
        return "redirect:/login";
    }

    private void addCaptcha(Model model, HttpSession session) {
        CaptchaService.CaptchaChallenge captcha = captchaService.generate(session);
        model.addAttribute("captchaImage", captcha.imageDataUri());
    }
}
