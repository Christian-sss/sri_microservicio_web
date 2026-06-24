package com.sri_web.sri_web.client;

import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class SriApiClient {

    private static final String API_COOKIE_SESSION_KEY = "SRI_API_COOKIE";

    private final WebClient webClient;

    public SriApiClient(WebClient sriApiWebClient) {
        this.webClient = sriApiWebClient;
    }

    public <T> T get(String uri, Class<T> responseType, HttpSession session) {
        return webClient.get()
                .uri(uri)
                .headers(headers -> addSessionCookie(headers, session))
                .exchangeToMono(response -> handleResponse(response, responseType, session))
                .block();
    }

    public <T> T get(String uri, ParameterizedTypeReference<T> responseType, HttpSession session) {
        return webClient.get()
                .uri(uri)
                .headers(headers -> addSessionCookie(headers, session))
                .exchangeToMono(response -> handleResponse(response, responseType, session))
                .block();
    }

    public <T> T post(String uri, Object body, Class<T> responseType, HttpSession session) {
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> addSessionCookie(headers, session))
                .bodyValue(body)
                .exchangeToMono(response -> handleResponse(response, responseType, session))
                .block();
    }

    public <T> T put(String uri, Object body, Class<T> responseType, HttpSession session) {
        return webClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> addSessionCookie(headers, session))
                .bodyValue(body)
                .exchangeToMono(response -> handleResponse(response, responseType, session))
                .block();
    }

    public byte[] getBytes(String uri, HttpSession session) {
        return webClient.get()
                .uri(uri)
                .headers(headers -> addSessionCookie(headers, session))
                .exchangeToMono(response -> handleResponse(response, byte[].class, session))
                .block();
    }

    public void clearSession(HttpSession session) {
        session.removeAttribute(API_COOKIE_SESSION_KEY);
    }

    private void addSessionCookie(HttpHeaders headers, HttpSession session) {
        Object cookie = session.getAttribute(API_COOKIE_SESSION_KEY);
        if (cookie != null) {
            headers.add(HttpHeaders.COOKIE, cookie.toString());
        }
    }

    private void saveSessionCookie(ClientResponse response, HttpSession session) {
        response.headers().header(HttpHeaders.SET_COOKIE).stream()
                .filter(cookie -> cookie.startsWith("JSESSIONID"))
                .findFirst()
                .map(cookie -> cookie.split(";", 2)[0])
                .ifPresent(cookie -> session.setAttribute(API_COOKIE_SESSION_KEY, cookie));
    }

    private <T> Mono<T> handleResponse(ClientResponse response, Class<T> responseType, HttpSession session) {
        saveSessionCookie(response, session);
        if (response.statusCode().isError()) {
            return toError(response);
        }
        return response.bodyToMono(responseType);
    }

    private <T> Mono<T> handleResponse(ClientResponse response, ParameterizedTypeReference<T> responseType, HttpSession session) {
        saveSessionCookie(response, session);
        if (response.statusCode().isError()) {
            return toError(response);
        }
        return response.bodyToMono(responseType);
    }

    private <T> Mono<T> toError(ClientResponse response) {
        HttpStatusCode status = response.statusCode();
        return response.bodyToMono(String.class)
                .defaultIfEmpty("Error consultando API")
                .flatMap(body -> Mono.error(new SriApiException(status.value(), body)));
    }
}
