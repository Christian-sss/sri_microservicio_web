<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/components/page-start.jsp" />
<section class="content-panel analytics-dashboard">
    <div class="stats-filter-bar">
        <div class="chart-title">
            <h3>Filtro de estadisticas</h3>
            <span>Selecciona un cultivo para revisar su consumo</span>
        </div>
        <form class="stats-filter-form" action="/estadisticas" method="get">
            <label class="stats-filter-select" for="selectEstadisticasCultivo">
                Cultivo
                <select id="selectEstadisticasCultivo" name="cultivoId" onchange="this.form.submit()">
                    <option value="">Todos los cultivos</option>
                    <c:forEach var="cultivo" items="${cultivos}">
                        <option value="${cultivo.id}" data-cultivo-name="${cultivo.nombre}" ${cultivo.id == selectedCultivoId ? 'selected' : ''}>${cultivo.nombre}</option>
                    </c:forEach>
                </select>
            </label>
        </form>
    </div>

    <div class="stat-card-row">
        <article class="industrial-kpi">
            <div class="kpi-icon irrigation"><i class="fa-solid fa-calendar-check"></i></div>
            <div class="kpi-copy"><span>Riegos del mes</span><strong>${empty resumen.totalRiegosMes ? 0 : resumen.totalRiegosMes}</strong><small>Total registrado</small></div>
        </article>
        <article class="industrial-kpi">
            <div class="kpi-icon moisture"><i class="fa-solid fa-hand"></i></div>
            <div class="kpi-copy"><span>Manuales</span><strong>${empty resumen.riegosManualesMes ? 0 : resumen.riegosManualesMes}</strong><small>Control directo</small></div>
        </article>
        <article class="industrial-kpi">
            <div class="kpi-icon crop"><i class="fa-solid fa-robot"></i></div>
            <div class="kpi-copy"><span>Automaticos</span><strong>${empty resumen.riegosAutomaticosMes ? 0 : resumen.riegosAutomaticosMes}</strong><small>Control por perfil</small></div>
        </article>
    </div>

    <div class="dashboard-grid">
        <article class="industrial-chart-card">
            <div class="chart-heading">
                <div><h3>Telemetria</h3><p>Humedad y distancia</p></div>
                <span class="chart-state"><i class="fa-solid fa-circle"></i> En vivo</span>
            </div>
            <div class="chart-frame"><canvas id="statsTelemetryChart" class="stats-canvas"></canvas></div>
            <div id="telemetryData" class="d-none">
                <c:forEach var="item" items="${telemetria}">
                    <span data-label="${item.etiqueta}" data-humidity="${item.humedad}" data-distance="${item.distancia}"></span>
                </c:forEach>
            </div>
        </article>

        <article class="industrial-chart-card">
            <div class="chart-heading">
                <div><h3>Distribucion de modos</h3><p>Manual vs automatico</p></div>
                <span class="chart-state"><i class="fa-solid fa-circle"></i> Mes actual</span>
            </div>
            <div class="chart-frame donut">
                <canvas id="statsModeChart" class="stats-canvas" data-manual="${empty distribucion.manual ? 0 : distribucion.manual}" data-auto="${empty distribucion.automatico ? 0 : distribucion.automatico}"></canvas>
            </div>
        </article>
    </div>
</section>


<section class="content-panel">
    <article class="industrial-chart-card">
        <div class="chart-heading">
            <div><h3>Consumo de agua</h3><p>Litros usados por cultivo</p></div>
            <span class="chart-state"><i class="fa-solid fa-circle"></i> Historico</span>
        </div>
        <div class="chart-frame water-consumption-chart"><canvas id="statsWaterChart" class="stats-canvas"></canvas></div>
        <div id="waterConsumptionData" class="d-none">
            <c:forEach var="item" items="${consumo}">
                <span data-label="${item.cultivo}" data-cultivo="${item.cultivo}" data-date="${item.fecha}" data-liters="${item.litrosConsumidos}"></span>
            </c:forEach>
        </div>
    </article>
</section>

<section class="content-panel table-panel">
    <div class="chart-title"><h3>Consumo por cultivo</h3><span id="statsConsumptionLabel">Detalle historico de riegos</span></div>
    <div class="table-wrap">
        <table>
            <thead><tr><th>Fecha</th><th>Cultivo</th><th>Inicio</th><th>Fin</th><th>Litros</th></tr></thead>
            <tbody>
            <c:forEach var="item" items="${consumo}">
                <tr data-consumo-row data-cultivo="${item.cultivo}"><td>${item.fecha}</td><td>${item.cultivo}</td><td>${item.horaInicio}</td><td>${item.horaFin}</td><td>${item.litrosConsumidos}</td></tr>
            </c:forEach>
            <tr id="statsConsumptionEmpty" class="d-none"><td colspan="5" class="empty-state">Sin registros para el cultivo seleccionado.</td></tr>
            <c:if test="${empty consumo}"><tr><td colspan="5" class="empty-state">Sin registros de consumo.</td></tr></c:if>
            </tbody>
        </table>
    </div>
</section>
<jsp:include page="/WEB-INF/jsp/components/page-end.jsp" />
