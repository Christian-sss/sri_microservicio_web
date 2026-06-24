<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/components/page-start.jsp" />
<section class="content-panel workspace-grid">
    <article class="chart-panel"><div class="chart-title"><h3>Consulta</h3><span>Asistente del sistema</span></div><form action="/chat" method="post" class="stack-form"><label>Mensaje<textarea name="mensaje" required>${pregunta}</textarea></label><button class="btn" type="submit"><i class="fa-solid fa-paper-plane"></i> Enviar</button></form></article>
<article class="chart-panel"><div class="chart-title"><h3>Respuesta</h3><span>Resultado de la consulta</span></div><c:choose><c:when test="${not empty respuesta}"><p class="assistant-answer">${respuesta}</p></c:when><c:otherwise><p class="empty-state">La respuesta aparecera aqui.</p></c:otherwise></c:choose></article>
</section>
<jsp:include page="/WEB-INF/jsp/components/page-end.jsp" />
