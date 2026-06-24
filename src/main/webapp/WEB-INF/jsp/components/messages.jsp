<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${not empty mensaje}">
    <div class="alert ok"><i class="fa-solid fa-circle-check"></i><span>${mensaje}</span></div>
    <span class="flash-message-source d-none" data-type="success" data-title="Operacion realizada">${mensaje}</span>
</c:if>
<c:if test="${not empty error}">
    <div class="alert error"><i class="fa-solid fa-triangle-exclamation"></i><span>${error}</span></div>
    <span class="flash-message-source d-none" data-type="danger" data-title="No se pudo completar">${error}</span>
</c:if>
