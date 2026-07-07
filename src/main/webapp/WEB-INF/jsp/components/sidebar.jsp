<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<aside class="sidebar">
    <div class="sidebar-logo-mark" aria-hidden="true"></div>
    <div class="brand">
        <div class="brand-icon"><i class="fa-solid fa-leaf"></i></div>
        <div class="brand-title">
            <h1>SRI Riego Inteligente</h1>
            <span>Control IoT agricola</span>
        </div>
    </div>

    <nav class="side-nav" aria-label="Navegacion principal">
        <a href="/dashboard" class="nav-item-link ${active == 'dashboard' ? 'active' : ''}" title="Dashboard">
            <i class="fa-solid fa-chart-line"></i><span class="nav-label">Dashboard</span>
        </a>
        <a href="/riego" class="nav-item-link ${active == 'riego' ? 'active' : ''}" title="Riego">
            <i class="fa-solid fa-water"></i><span class="nav-label">Riego</span>
        </a>
        <a href="/cultivos" class="nav-item-link ${active == 'cultivos' ? 'active' : ''}" title="Cultivos">
            <i class="fa-solid fa-seedling"></i><span class="nav-label">Cultivos</span>
        </a>
        <a href="/estadisticas" class="nav-item-link ${active == 'estadisticas' ? 'active' : ''}" title="Estadisticas">
            <i class="fa-solid fa-chart-bar"></i><span class="nav-label">Estadisticas</span>
        </a>
        <a href="/reportes" class="nav-item-link ${active == 'reportes' ? 'active' : ''}" title="Reportes">
            <i class="fa-solid fa-file-lines"></i><span class="nav-label">Reportes</span>
        </a>

    </nav>

    <div class="sidebar-footer">
        <a href="/mqtt" class="settings-link ${active == 'mqtt' ? 'active' : ''}" title="Configuracion Tecnica">
            <i class="fa-solid fa-cog"></i><span class="logout-label">Configuracion Tecnica</span>
        </a>
        <form action="/logout" method="post" class="logout-form-clean">
            <button class="logout-link" type="submit" title="Cerrar Sesion">
                <i class="fa-solid fa-right-from-bracket"></i><span class="logout-label">Cerrar Sesion</span>
            </button>
        </form>
    </div>
</aside>
