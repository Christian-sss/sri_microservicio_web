<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/components/page-start.jsp" />
<section class="content-panel">
    <article class="card riego-control-card shadow-sm">
        <div class="card-header">
            <h3>
                <i class="fa-solid fa-faucet-drip me-2"></i>
                Centro de Control de Riego
            </h3>
            <p>Orquesta el modo de operacion y las ordenes directas de bomba.</p>
        </div>

        <div class="card-body p-4">
            <section class="operation-mode">
                <span class="section-label">Modo de Operacion</span>

                <form id="formModoRiego" action="/riego/modo" method="post">
                    <input id="inputModoRiego" type="hidden" name="modo" value="${estado.automatico ? 'MANUAL' : 'AUTOMATICO'}">
                    <div class="mode-switch-wrapper">
                        <span class="mode-caption">MANUAL</span>
                        <div class="form-check form-switch form-switch-xl">
                            <input class="form-check-input" type="checkbox" role="switch" id="switchModoOperacion" ${estado.automatico ? 'checked' : ''}>
                        </div>
                        <span id="textoModoOperacion" class="mode-status ${estado.automatico ? 'automatico' : 'manual'}">${estado.automatico ? 'AUTOMATICO' : 'MANUAL'}</span>
                    </div>
                </form>
            </section>

            <div id="alertaModoAutomatico" class="alert alert-info mt-4 ${estado.automatico ? '' : 'd-none'}">
                <i class="fa-solid fa-circle-info me-2"></i>
                El riego automatico usa el perfil de cultivo seleccionado.
            </div>

            <section class="mt-4">
                <span class="section-label">Controles Manuales</span>
                <form action="/riego/manual" method="post" class="profile-selector mt-3">
                    <label class="form-label fw-bold text-success" for="selectManualCultivo">Cultivo a regar</label>
                    <select id="selectManualCultivo" class="form-select profile-select" name="cultivoId" required ${estado.automatico ? 'disabled' : ''}>
                        <c:forEach var="cultivo" items="${cultivos}">
                            <option value="${cultivo.id}">${cultivo.nombre}</option>
                        </c:forEach>
                    </select>
                    <small class="profile-help">Solo aparecen cultivos activos</small>

                    <div class="manual-actions mt-3">
                        <button id="btnEncenderBomba" class="btn btn-success btn-lg" name="orden" value="ON" type="submit" ${estado.automatico ? 'disabled' : ''}>
                            <i class="fa-solid fa-power-off me-2"></i>
                            Encender Bomba
                        </button>
                        <button id="btnApagarBomba" class="btn btn-danger btn-lg" name="orden" value="OFF" type="submit" ${estado.automatico ? 'disabled' : ''}>
                            <i class="fa-solid fa-ban me-2"></i>
                            Apagar Bomba
                        </button>
                    </div>
                </form>
            </section>
        </div>
    </article>

    <article class="card riego-control-card shadow-sm mt-4">
        <div class="card-header">
            <h3>
                <i class="fa-solid fa-clock me-2"></i>
                Programacion Automatica
            </h3>
            <p>Define que cultivo gobierna el motor automatico y a que hora debe evaluar el riego.</p>
        </div>

        <div class="card-body p-4">
            <form action="/riego/programacion" method="post" class="row g-3 align-items-end">
                <div class="col-12 col-lg-5">
                    <label class="form-label fw-bold text-success" for="selectProgramacionCultivo">Seleccionar Cultivo</label>
                    <select id="selectProgramacionCultivo" class="form-select profile-select" name="cultivoId" required>
                        <c:forEach var="cultivo" items="${cultivos}">
                            <option value="${cultivo.id}" ${cultivo.id == estado.cultivoActivoId ? 'selected' : ''}>${cultivo.nombre}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-12 col-lg-4">
                    <label class="form-label fw-bold text-success" for="inputHoraRiego">Hora de Riego</label>
                    <input id="inputHoraRiego" name="horaRiego" type="time" class="form-control schedule-input" value="${estado.horaRiegoProgramada}">
                </div>

                <div class="col-12 col-lg-3 d-grid">
                    <button id="btnProgramarRiego" class="btn btn-success btn-lg schedule-button" type="submit">
                        <i class="fa-solid fa-calendar-check me-2"></i>
                        Programar Riego
                    </button>
                </div>
            </form>
        </div>
    </article>
</section>
<jsp:include page="/WEB-INF/jsp/components/page-end.jsp" />
