package com.sri_web.sri_web.dto;

public record CultivoRequest(
        String nombre,
        Integer humedadMinOptima,
        Integer humedadMaxOptima,
        Integer duracionRiegoMinutos,
        String tratoRecomendado
) {
}
