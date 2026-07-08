<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/components/page-start.jsp"/>
<section class="content-panel workspace-grid">
    <article class="chart-panel">
        <div class="chart-title"><h3>Estado MQTT</h3><span>Conexion con broker IoT</span></div>
        <p><span class="badge ${mqtt.conectado ? 'ok' : 'off'}">${mqtt.conectado ? 'Conectado' : 'Desconectado'}</span>
        </p>
    </article>
    <article class="chart-panel">
        <div class="chart-title"><h3>Credenciales</h3><span>Conexion tecnica</span></div>
        <form action="/mqtt/connect" method="post" class="stack-form"><label>Usuario<input name="username"
                                                                                           required></label><label>Contrasena<input
                type="password" name="password" required></label>
            <button class="btn" type="submit"><i class="fa-solid fa-plug"></i> Conectar</button>
        </form>
        <form action="/mqtt/disconnect" method="post" class="form-separated">
            <button class="btn danger" type="submit"><i class="fa-solid fa-link-slash"></i> Desconectar</button>
        </form>
    </article>
</section>
<jsp:include page="/WEB-INF/jsp/components/page-end.jsp"/>
