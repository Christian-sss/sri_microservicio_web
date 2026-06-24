<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/components/page-start.jsp" />
<section class="content-panel">
    <div class="chart-header align-items-center">
        <div class="chart-title">
            <h3>Perfiles de cultivo</h3>
            <span>Gestion de perfiles disponibles para riego</span>
        </div>
        <button class="btn btn-success btn-new-profile" type="button" data-bs-toggle="modal" data-bs-target="#modalNuevoCultivo">
            <i class="fa-solid fa-plus"></i> Nuevo cultivo
        </button>
    </div>

    <div class="cultivos-toolbar">
        <div class="search-box input-group">
            <span class="input-group-text"><i class="fa-solid fa-magnifying-glass"></i></span>
            <input id="inputBuscarCultivo" type="search" class="form-control" placeholder="Buscar cultivo">
        </div>
        <div class="filter-pills" role="group" aria-label="Filtrar cultivos">
            <button class="filter-pill active" type="button" data-cultivo-filter="todos">Todos</button>
            <button class="filter-pill" type="button" data-cultivo-filter="activos">Habilitados</button>
            <button class="filter-pill" type="button" data-cultivo-filter="inactivos">Deshabilitados</button>
        </div>
    </div>

    <div id="cultivosGrid" class="cultivo-card-grid">
        <c:forEach var="cultivo" items="${cultivos}">
            <article class="cultivo-grid-card ${cultivo.activo ? '' : 'inactive'}"
                     data-cultivo-card
                     data-name="${cultivo.nombre}"
                     data-active="${cultivo.activo}">
                <div class="d-flex justify-content-between align-items-start gap-3">
                    <div>
                        <h3 class="cultivo-title">${cultivo.nombre}</h3>
                        <div class="cultivo-detail"><i class="fa-solid fa-droplet"></i> ${cultivo.humedadMinOptima}% - ${cultivo.humedadMaxOptima}%</div>
                        <div class="cultivo-detail"><i class="fa-solid fa-clock"></i> ${cultivo.duracionRiegoMinutos} minutos</div>
                    </div>
                    <span class="badge ${cultivo.activo ? 'ok' : 'off'}">${cultivo.activo ? 'Habilitado' : 'Deshabilitado'}</span>
                </div>
                <p class="cultivo-notes">${empty cultivo.tratoRecomendado ? 'Sin recomendaciones registradas.' : cultivo.tratoRecomendado}</p>
                <form class="cultivo-card-actions" action="/cultivos/${cultivo.id}/toggle" method="post">
                    <button class="btn btn-outline-success" type="submit">
                        <i class="fa-solid fa-toggle-on"></i> ${cultivo.activo ? 'Deshabilitar' : 'Habilitar'}
                    </button>
                </form>
            </article>
        </c:forEach>
    </div>

    <div id="emptyCultivos" class="empty-state d-none">
        <i class="fa-solid fa-seedling"></i>
        <strong>No hay cultivos para este filtro.</strong>
    </div>
    <c:if test="${empty cultivos}">
        <div class="empty-state">
            <i class="fa-solid fa-seedling"></i>
            <strong>No hay cultivos registrados.</strong>
        </div>
    </c:if>
</section>

<div class="modal fade" id="modalNuevoCultivo" tabindex="-1" aria-labelledby="modalNuevoCultivoLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content cultivo-modal">
            <div class="modal-header">
                <div>
                    <h5 class="modal-title" id="modalNuevoCultivoLabel">Nuevo cultivo</h5>
                    <p class="modal-subtitle">Completa los datos del perfil de riego</p>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <form action="/cultivos" method="post">
                <div class="modal-body">
                    <div class="form-grid">
                        <label>Nombre
                            <input name="nombre" required>
                        </label>
                        <label>Duracion de riego
                            <input type="number" name="duracionRiegoMinutos" min="1" required>
                        </label>
                        <label>Humedad minima
                            <input type="number" name="humedadMinOptima" min="0" max="100" required>
                        </label>
                        <label>Humedad maxima
                            <input type="number" name="humedadMaxOptima" min="0" max="100" required>
                        </label>
                        <label class="span-2">Trato recomendado
                            <textarea name="tratoRecomendado"></textarea>
                        </label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-success" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-success"><i class="fa-solid fa-floppy-disk"></i> Guardar cultivo</button>
                </div>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/components/page-end.jsp" />
