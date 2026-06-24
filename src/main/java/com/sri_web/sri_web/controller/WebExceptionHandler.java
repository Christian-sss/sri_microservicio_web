package com.sri_web.sri_web.controller;

import com.sri_web.sri_web.client.SriApiException;
import com.sri_web.sri_web.service.CaptchaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class WebExceptionHandler {

    private final CaptchaService captchaService;

    public WebExceptionHandler(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @ExceptionHandler(SriApiException.class)
    public ModelAndView handleApiError(SriApiException exception, HttpSession session) {
        if (exception.getStatus() == 401) {
            session.removeAttribute("usuario");
            return login("Tu sesion expiro. Inicia sesion nuevamente.", session);
        }

        return error("No se pudo completar la solicitud. Codigo " + exception.getStatus() + ".");
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ModelAndView handleConnectionError(WebClientRequestException exception) {
        return error("No se pudo conectar con el servicio del sistema. Verifica que este encendido y configurado correctamente.");
    }

    private ModelAndView login(String message, HttpSession session) {
        ModelAndView view = new ModelAndView("login");
        view.addObject("error", message);
        view.addObject("captchaImage", captchaService.generate(session).imageDataUri());
        return view;
    }

    private ModelAndView error(String message) {
        ModelAndView view = new ModelAndView("error");
        view.addObject("message", message);
        return view;
    }
}
