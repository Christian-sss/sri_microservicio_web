package com.sri_web.sri_web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FrontendAuthConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns(
                        "/dashboard",
                        "/cultivos/**",
                        "/riego/**",
                        "/estadisticas/**",
                        "/reportes/**",
                        "/mqtt/**",
                        "/chat/**",
                        "/web-api/**"
                );
    }

    private static class SessionInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if (request.getSession().getAttribute("usuario") != null) {
                return true;
            }

            response.sendRedirect("/login");
            return false;
        }
    }
}