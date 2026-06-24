<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header class="topbar">
    <div class="topbar-left">
        <button class="sidebar-toggle" id="sidebarToggle" type="button" aria-label="Alternar barra lateral">
            <i class="fa-solid fa-bars"></i>
        </button>
        <div class="page-title">
            <h2>${pageTitle}</h2>
            <p>${pageSubtitle}</p>
        </div>
    </div>

    <div class="profile-area">
        <span class="status-badge"><i class="fa-solid fa-cloud-arrow-up"></i> Sistema activo</span>
        <span class="admin-badge"><i class="fa-solid fa-user"></i> ${not empty sessionScope.usuario.nombre ? sessionScope.usuario.nombre : 'Usuario'}</span>
    </div>
</header>
