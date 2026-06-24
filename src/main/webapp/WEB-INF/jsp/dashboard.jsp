<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/components/page-start.jsp" />
<section class="content-panel">
    <div class="metric-grid">
        <article class="metric-card" data-metric="humidity">
            <i class="metric-icon fa-solid fa-gauge-high"></i>
            <p class="metric-label">Humedad del suelo</p>
            <p class="metric-value"><span id="humidityValue" data-value="${empty dashboard.humedad ? 0 : dashboard.humedad}">${empty dashboard.humedad ? '--' : dashboard.humedad}</span><span id="humidityUnit">%</span></p>
            <span class="metric-sub" id="humidityLabel">Lectura recibida desde sensores</span>
        </article>
        <article class="metric-card" data-metric="water">
            <i class="metric-icon fa-solid fa-circle-check"></i>
            <p class="metric-label">Nivel de agua</p>
            <p class="metric-value fs-4"><span id="waterDistance" data-value="${empty dashboard.distancia ? 0 : dashboard.distancia}">${empty dashboard.distancia ? '--' : dashboard.distancia}</span> <span class="fs-6">cm</span></p>
            <span class="metric-sub" id="waterLabel">Distancia del sensor ultrasonico</span>
        </article>
        <article class="metric-card" data-metric="pump">
            <i class="metric-icon fa-solid fa-pump-soap"></i>
            <p class="metric-label">Bomba</p>
            <p class="metric-value fs-4"><span id="pumpState" class="state-pill manual">${empty estadoRiego.bomba ? 'Sin actividad' : estadoRiego.bomba}</span></p>
            <span class="metric-sub" id="pumpLabel">Estado actual del sistema</span>
        </article>
        <article class="metric-card" data-metric="mode">
            <i class="metric-icon fa-solid fa-robot"></i>
            <p class="metric-label">Modo</p>
            <p class="metric-value fs-4"><span id="irrigationMode" class="state-pill manual"><i class="fa-solid fa-hand"></i> ${empty estadoRiego.modo ? '--' : estadoRiego.modo}</span></p>
            <span class="metric-sub" id="modeLabel">Configuracion activa</span>
        </article>
    </div>

    <div class="chart-panel" id="chartPanel">
        <div class="chart-header">
            <div class="chart-title">
                <h3>Humedad en tiempo real</h3>
                <span>Lecturas recientes del sensor</span>
            </div>
            <span class="live-note"><span class="live-dot"></span> En vivo</span>
        </div>
        <canvas id="humidityChart" class="chart-canvas" data-humidity="${empty dashboard.humedad ? 0 : dashboard.humedad}"></canvas>
    </div>
</section>

<section class="content-panel dashboard-grid">
    <article class="chart-panel">
        <div class="chart-title"><h3>Cultivo seleccionado</h3><span>Perfil usado para la decision de riego</span></div>
        <p class="focus-value">${empty estadoRiego.cultivoSeleccionado ? 'Sin seleccion' : estadoRiego.cultivoSeleccionado}</p>
        <p class="muted">El sistema conserva la seleccion y se comunica con MQTT cuando corresponde.</p>
    </article>
    <article class="chart-panel">
        <div class="chart-title"><h3>Resumen operativo</h3><span>Estado general del sistema</span></div>
        <div class="status-list">
            <div><span>Modo</span><strong>${empty estadoRiego.modo ? '--' : estadoRiego.modo}</strong></div>
            <div><span>Bomba</span><strong>${empty estadoRiego.bomba ? '--' : estadoRiego.bomba}</strong></div>
            <div><span>Programacion</span><strong>${empty estadoRiego.horaProgramada ? '--' : estadoRiego.horaProgramada}</strong></div>
        </div>
    </article>
</section>
<jsp:include page="/WEB-INF/jsp/components/page-end.jsp" />
