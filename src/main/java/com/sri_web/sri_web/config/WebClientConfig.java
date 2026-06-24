package com.sri_web.sri_web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient sriApiWebClient(@Value("${sri.api.base-url}") String apiBaseUrl) {
        return WebClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
    }
}
