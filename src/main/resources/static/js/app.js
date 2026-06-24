(() => {
    const toggle = document.getElementById('sidebarToggle');
    const humidityCanvas = document.getElementById('humidityChart');
    const chartPanel = document.getElementById('chartPanel');
    const cards = Array.from(document.querySelectorAll('.metric-card'));
    let dashboardTelemetry = [];
    let liveTimer = null;
    let lastAlertId = null;

    if (toggle) {
        toggle.addEventListener('click', () => {
            if (window.innerWidth <= 780) {
                document.body.classList.toggle('sidebar-open');
                return;
            }
            document.body.classList.toggle('sidebar-collapsed');
        });
    }

    document.querySelectorAll('[data-confirm]').forEach(button => {
        button.addEventListener('click', event => {
            const message = button.dataset.confirm || 'Confirmar accion?';
            if (!window.confirm(message)) event.preventDefault();
        });
    });



    function showToastNotification(type, titleText, messageText) {
        const toastElement = document.getElementById('sriAlertToast');
        const header = document.getElementById('sriAlertToastHeader');
        const icon = document.getElementById('sriAlertToastIcon');
        const title = document.getElementById('sriAlertToastTitle');
        const body = document.getElementById('sriAlertToastBody');
        if (!toastElement || !header || !icon || !title || !body || !window.bootstrap) return;

        const normalizedType = type === 'success' ? 'success' : (type || 'info');
        header.classList.remove('warning', 'danger', 'info', 'success');
        header.classList.add(normalizedType);
        icon.className = normalizedType === 'danger'
            ? 'fa-solid fa-triangle-exclamation me-2'
            : 'fa-solid fa-circle-check me-2';
        title.textContent = titleText || 'Aviso del sistema';
        body.textContent = messageText || '';

        bootstrap.Toast.getOrCreateInstance(toastElement, { delay: 6500 }).show();
    }

    function showFlashNotifications() {
        document.querySelectorAll('.flash-message-source').forEach(source => {
            const message = source.textContent ? source.textContent.trim() : '';
            if (!message) return;
            showToastNotification(source.dataset.type, source.dataset.title, message);
        });
    }


    function animateCards() {
        cards.forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(14px)';
            window.setTimeout(() => {
                card.style.transition = 'opacity .35s ease, transform .35s ease, box-shadow .2s ease';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, 80 * index);
        });
    }

    function numberFrom(value, fallback) {
        const parsed = Number(value);
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    function text(id, value) {
        const element = document.getElementById(id);
        if (element) element.textContent = value;
    }

    function normalizeText(value) {
        return (value || '').toString().trim().toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
    }

    function selectedStatsCropName() {
        const select = document.getElementById('selectEstadisticasCultivo');
        if (!select || !select.value) return '';
        return select.selectedOptions[0]?.dataset.cultivoName || select.selectedOptions[0]?.textContent || '';
    }

    function setupCanvas(canvas) {
        const ctx = canvas.getContext('2d');
        const rect = canvas.getBoundingClientRect();
        const ratio = window.devicePixelRatio || 1;
        canvas.width = Math.max(1, rect.width * ratio);
        canvas.height = Math.max(1, rect.height * ratio);
        ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
        return { ctx, width: canvas.clientWidth, height: canvas.clientHeight };
    }

    function normalizeTelemetry(items, fallbackHumidity) {
        const rows = Array.isArray(items) ? items : [];
        const normalized = rows
            .map(item => ({
                label: item.etiqueta || item.fecha || item.hora || '',
                humidity: numberFrom(item.humedad, null),
                distance: numberFrom(item.distancia, null)
            }))
            .filter(item => item.humidity !== null || item.distance !== null)
            .slice(-12);

        if (normalized.length) return normalized;

        return [{
            label: new Date().toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' }),
            humidity: numberFrom(fallbackHumidity, 0),
            distance: null
        }];
    }

    function drawDashboardHumidity() {
        if (!humidityCanvas) return;
        const { ctx, width, height } = setupCanvas(humidityCanvas);
        const points = dashboardTelemetry.length
            ? dashboardTelemetry
            : normalizeTelemetry([], humidityCanvas.dataset.humidity);
        const pad = { left: 54, right: 26, top: 18, bottom: 38 };
        const graphW = width - pad.left - pad.right;
        const graphH = height - pad.top - pad.bottom;
        const xFor = index => pad.left + (index / Math.max(1, points.length - 1)) * graphW;
        const yFor = value => pad.top + graphH - (Math.max(0, Math.min(100, value)) / 100) * graphH;

        ctx.clearRect(0, 0, width, height);
        ctx.fillStyle = '#ffffff';
        ctx.fillRect(0, 0, width, height);
        ctx.font = '700 11px Inter, sans-serif';
        ctx.textBaseline = 'middle';
        ctx.lineWidth = 1;

        [0, 25, 50, 75, 100].forEach(tick => {
            const y = yFor(tick);
            ctx.strokeStyle = '#dcebd5';
            ctx.beginPath(); ctx.moveTo(pad.left, y); ctx.lineTo(width - pad.right, y); ctx.stroke();
            ctx.fillStyle = '#7f9a76'; ctx.textAlign = 'right'; ctx.fillText(tick + '%', pad.left - 10, y);
        });

        ctx.beginPath();
        points.forEach((point, index) => {
            const x = xFor(index);
            const y = yFor(numberFrom(point.humidity, 0));
            if (index === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y);
        });
        ctx.strokeStyle = '#2f7d32';
        ctx.lineWidth = 3;
        ctx.stroke();

        const lastX = xFor(points.length - 1);
        ctx.lineTo(lastX, height - pad.bottom);
        ctx.lineTo(pad.left, height - pad.bottom);
        ctx.closePath();
        const gradient = ctx.createLinearGradient(0, pad.top, 0, height - pad.bottom);
        gradient.addColorStop(0, 'rgba(79, 163, 66, .28)');
        gradient.addColorStop(1, 'rgba(79, 163, 66, 0)');
        ctx.fillStyle = gradient;
        ctx.fill();

        points.forEach((point, index) => {
            const x = xFor(index);
            const y = yFor(numberFrom(point.humidity, 0));
            ctx.beginPath(); ctx.arc(x, y, 4, 0, Math.PI * 2);
            ctx.fillStyle = '#fff'; ctx.fill(); ctx.strokeStyle = '#2f7d32'; ctx.lineWidth = 2; ctx.stroke();
        });

        ctx.fillStyle = '#7f9a76';
        ctx.textAlign = 'center';
        points.forEach((point, index) => {
            if (index % Math.ceil(points.length / 4) === 0 || index === points.length - 1) {
                ctx.fillText(point.label || '', xFor(index), height - 18);
            }
        });
    }



    function showSystemAlert(alerta) {
        if (!alerta || alerta.id == null) return;
        const alertId = String(alerta.id);
        if (lastAlertId === alertId) return;
        lastAlertId = alertId;

        const toastElement = document.getElementById('sriAlertToast');
        const header = document.getElementById('sriAlertToastHeader');
        const icon = document.getElementById('sriAlertToastIcon');
        const title = document.getElementById('sriAlertToastTitle');
        const body = document.getElementById('sriAlertToastBody');
        if (!toastElement || !header || !icon || !title || !body || !window.bootstrap) return;

        const type = alerta.tipo || 'info';
        header.classList.remove('warning', 'danger', 'info');
        header.classList.add(type);
        icon.className = type === 'danger'
            ? 'fa-solid fa-droplet-slash me-2'
            : 'fa-solid fa-circle-check me-2';
        title.textContent = alerta.titulo || 'Aviso del sistema';
        body.textContent = alerta.mensaje || '';

        bootstrap.Toast.getOrCreateInstance(toastElement, { delay: 8000 }).show();
    }


    function updateDashboardValues(payload) {
        const dashboard = payload.dashboard || {};
        const estado = payload.estadoRiego || {};
        const humedad = dashboard.humedad ?? '--';
        showSystemAlert(dashboard.alerta);
        const distancia = dashboard.distancia ?? '--';

        text('humidityValue', humedad);
        text('waterDistance', distancia);
        text('pumpState', estado.bomba || 'Sin actividad');
        const mode = document.getElementById('irrigationMode');
        if (mode) mode.innerHTML = '<i class="fa-solid fa-hand"></i> ' + (estado.modo || '--');

        if (humidityCanvas && dashboard.humedad !== undefined) humidityCanvas.dataset.humidity = dashboard.humedad;
        dashboardTelemetry = normalizeTelemetry(payload.telemetria, dashboard.humedad);
        drawDashboardHumidity();

        if (chartPanel) {
            chartPanel.classList.add('is-refreshing');
            setTimeout(() => chartPanel.classList.remove('is-refreshing'), 900);
        }
    }

    async function loadDashboardLive() {
        try {
            const response = await fetch('/web-api/dashboard/live', { headers: { Accept: 'application/json' } });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const payload = await response.json();
            if (humidityCanvas) {
                updateDashboardValues(payload);
                return;
            }
            const dashboard = payload.dashboard || {};
            showSystemAlert(dashboard.alerta);
        } catch (error) {
            drawDashboardHumidity();
        }
    }

    function startDashboardLive() {
        if (humidityCanvas) {
            dashboardTelemetry = normalizeTelemetry([], humidityCanvas.dataset.humidity);
            drawDashboardHumidity();
        }
        loadDashboardLive();
        liveTimer = window.setInterval(loadDashboardLive, 5000);
        window.addEventListener('beforeunload', () => window.clearInterval(liveTimer));
        if (humidityCanvas) window.addEventListener('resize', drawDashboardHumidity);
    }

    function drawStatsTelemetry() {
        const canvas = document.getElementById('statsTelemetryChart');
        if (!canvas) return;
        const rows = Array.from(document.querySelectorAll('#telemetryData span'));
        const data = rows.map(row => ({
            label: row.dataset.label || '',
            humidity: numberFrom(row.dataset.humidity, 0),
            distance: numberFrom(row.dataset.distance, 0)
        }));
        const points = data.length ? data : [{ label: 'Sin datos', humidity: 0, distance: 0 }, { label: 'Sin datos', humidity: 0, distance: 0 }];
        const { ctx, width, height } = setupCanvas(canvas);
        const pad = { left: 48, right: 28, top: 22, bottom: 42 };
        const graphW = width - pad.left - pad.right;
        const graphH = height - pad.top - pad.bottom;
        const maxDistance = Math.max(100, ...points.map(item => item.distance));
        const xFor = index => pad.left + (index / Math.max(1, points.length - 1)) * graphW;
        const yHumidity = value => pad.top + graphH - (value / 100) * graphH;
        const yDistance = value => pad.top + graphH - (value / maxDistance) * graphH;

        ctx.clearRect(0, 0, width, height);
        ctx.fillStyle = '#fff'; ctx.fillRect(0, 0, width, height);
        ctx.font = '700 11px Inter, sans-serif';
        [0, 25, 50, 75, 100].forEach(tick => {
            const y = yHumidity(tick);
            ctx.strokeStyle = '#e2e8f0'; ctx.beginPath(); ctx.moveTo(pad.left, y); ctx.lineTo(width - pad.right, y); ctx.stroke();
            ctx.fillStyle = '#64748b'; ctx.textAlign = 'right'; ctx.fillText(tick + '%', pad.left - 8, y + 3);
        });
        function drawLine(values, yFor, color) {
            ctx.beginPath();
            values.forEach((value, index) => { const x = xFor(index); const y = yFor(value); if (index === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y); });
            ctx.strokeStyle = color; ctx.lineWidth = 3; ctx.stroke();
        }
        drawLine(points.map(item => item.humidity), yHumidity, '#168aad');
        drawLine(points.map(item => item.distance), yDistance, '#16a34a');
        ctx.fillStyle = '#168aad'; ctx.textAlign = 'left'; ctx.fillText('Humedad', pad.left, 16);
        ctx.fillStyle = '#16a34a'; ctx.fillText('Distancia', pad.left + 86, 16);
    }

    function drawStatsMode() {
        const canvas = document.getElementById('statsModeChart');
        if (!canvas) return;
        const manual = numberFrom(canvas.dataset.manual, 0);
        const automatico = numberFrom(canvas.dataset.auto, 0);
        const total = Math.max(1, manual + automatico);
        const { ctx, width, height } = setupCanvas(canvas);
        const cx = width / 2, cy = height / 2 - 8;
        const radius = Math.min(width, height) * 0.28;
        const thickness = Math.max(28, radius * 0.38);
        ctx.clearRect(0, 0, width, height);
        let start = -Math.PI / 2;
        [{ value: manual, color: '#2563eb' }, { value: automatico, color: '#16a34a' }].forEach(part => {
            const angle = (part.value / total) * Math.PI * 2;
            ctx.beginPath(); ctx.arc(cx, cy, radius, start, start + angle); ctx.lineWidth = thickness; ctx.strokeStyle = part.color; ctx.lineCap = 'round'; ctx.stroke();
            start += angle;
        });
        if (manual + automatico === 0) { ctx.beginPath(); ctx.arc(cx, cy, radius, 0, Math.PI * 2); ctx.lineWidth = thickness; ctx.strokeStyle = '#d9e2ec'; ctx.stroke(); }
        ctx.fillStyle = '#0f172a'; ctx.font = '800 28px Inter, sans-serif'; ctx.textAlign = 'center'; ctx.fillText(String(manual + automatico), cx, cy + 8);
        ctx.fillStyle = '#64748b'; ctx.font = '800 11px Inter, sans-serif'; ctx.fillText('RIEGOS', cx, cy + 30);
    }



    function drawStatsWater() {
        const canvas = document.getElementById('statsWaterChart');
        if (!canvas) return;

        const rows = Array.from(document.querySelectorAll('#waterConsumptionData span'));
        const selectedCrop = normalizeText(selectedStatsCropName());
        const shouldFilterRows = selectedCrop
            && rows.some(row => normalizeText(row.dataset.cultivo || row.dataset.label) === selectedCrop);
        const grouped = new Map();
        rows
            .filter(row => !shouldFilterRows || normalizeText(row.dataset.cultivo || row.dataset.label) === selectedCrop)
            .forEach(row => {
            const label = row.dataset.label || 'Sin cultivo';
            const liters = numberFrom(row.dataset.liters, 0);
            grouped.set(label, (grouped.get(label) || 0) + liters);
        });

        const data = Array.from(grouped, ([label, liters]) => ({ label, liters }))
            .sort((a, b) => b.liters - a.liters)
            .slice(0, 8);
        const points = data.length ? data : [{ label: 'Sin datos', liters: 0 }];
        const { ctx, width, height } = setupCanvas(canvas);
        const pad = { left: 58, right: 22, top: 26, bottom: 54 };
        const graphW = width - pad.left - pad.right;
        const graphH = height - pad.top - pad.bottom;
        const maxLiters = Math.max(1, ...points.map(item => item.liters));
        const barGap = 12;
        const barW = Math.max(22, (graphW - barGap * (points.length - 1)) / points.length);

        ctx.clearRect(0, 0, width, height);
        ctx.fillStyle = '#fff';
        ctx.fillRect(0, 0, width, height);
        ctx.font = '700 11px Inter, sans-serif';

        [0, .25, .5, .75, 1].forEach(step => {
            const value = maxLiters * step;
            const y = pad.top + graphH - step * graphH;
            ctx.strokeStyle = '#e2e8f0';
            ctx.beginPath(); ctx.moveTo(pad.left, y); ctx.lineTo(width - pad.right, y); ctx.stroke();
            ctx.fillStyle = '#64748b'; ctx.textAlign = 'right'; ctx.fillText(value.toFixed(1), pad.left - 8, y + 3);
        });

        points.forEach((item, index) => {
            const x = pad.left + index * (barW + barGap);
            const h = (item.liters / maxLiters) * graphH;
            const y = pad.top + graphH - h;
            const gradient = ctx.createLinearGradient(0, y, 0, pad.top + graphH);
            gradient.addColorStop(0, '#2563eb');
            gradient.addColorStop(1, '#16a34a');
            ctx.fillStyle = gradient;
            ctx.beginPath();
            ctx.roundRect(x, y, barW, h, 8);
            ctx.fill();
            ctx.fillStyle = '#0f172a';
            ctx.textAlign = 'center';
            ctx.font = '800 11px Inter, sans-serif';
            ctx.fillText(item.liters.toFixed(1), x + barW / 2, y - 8);
            ctx.save();
            ctx.translate(x + barW / 2, height - 18);
            ctx.rotate(-Math.PI / 7);
            ctx.fillStyle = '#64748b';
            ctx.font = '800 10px Inter, sans-serif';
            ctx.fillText(item.label.slice(0, 14), 0, 0);
            ctx.restore();
        });

        ctx.fillStyle = '#64748b';
        ctx.textAlign = 'left';
        ctx.font = '800 11px Inter, sans-serif';
        ctx.fillText('Litros', 10, pad.top + 4);
    }


    function drawStatsCharts() {
        drawStatsTelemetry();
        drawStatsMode();
        drawStatsWater();
        window.addEventListener('resize', () => { drawStatsTelemetry(); drawStatsMode(); drawStatsWater(); });
    }


    function setupStatsCropFilter() {
        const select = document.getElementById('selectEstadisticasCultivo');
        const rows = Array.from(document.querySelectorAll('[data-consumo-row]'));
        const empty = document.getElementById('statsConsumptionEmpty');
        const label = document.getElementById('statsConsumptionLabel');
        if (!select) return;

        function applyFilter() {
            const selectedName = selectedStatsCropName();
            const selectedCrop = normalizeText(selectedName);
            const shouldFilterRows = selectedCrop
                && rows.some(row => normalizeText(row.dataset.cultivo) === selectedCrop);
            let visible = 0;
            rows.forEach(row => {
                const show = !shouldFilterRows || normalizeText(row.dataset.cultivo) === selectedCrop;
                row.classList.toggle('d-none', !show);
                if (show) visible += 1;
            });
            if (empty) empty.classList.toggle('d-none', visible !== 0);
            if (label) {
                label.textContent = selectedName
                    ? 'Detalle historico de riegos para ' + selectedName
                    : 'Detalle historico de riegos';
            }
            drawStatsWater();
        }

        select.addEventListener('change', applyFilter);
        applyFilter();
    }



    function setupRiegoPanel() {
        const switchModo = document.getElementById('switchModoOperacion');
        const formModo = document.getElementById('formModoRiego');
        const inputModo = document.getElementById('inputModoRiego');

        if (!switchModo || !formModo || !inputModo) return;

        switchModo.addEventListener('change', () => {
            inputModo.value = switchModo.checked ? 'AUTOMATICO' : 'MANUAL';
            formModo.submit();
        });
    }


    function setupCultivoFilters() {
        const search = document.getElementById('inputBuscarCultivo');
        const cultivoCards = Array.from(document.querySelectorAll('[data-cultivo-card]'));
        const empty = document.getElementById('emptyCultivos');
        const filters = Array.from(document.querySelectorAll('[data-cultivo-filter]'));
        let currentFilter = 'todos';
        if (!cultivoCards.length) return;
        function applyFilters() {
            const query = normalizeText(search ? search.value : '');
            let visible = 0;
            cultivoCards.forEach(card => {
                const name = normalizeText(card.dataset.name);
                const active = card.dataset.active === 'true';
                const show = (!query || name.includes(query)) && (currentFilter === 'todos' || (currentFilter === 'activos' && active) || (currentFilter === 'inactivos' && !active));
                card.classList.toggle('is-hidden', !show);
                if (show) visible += 1;
            });
            if (empty) empty.classList.toggle('d-none', visible !== 0);
        }
        filters.forEach(button => button.addEventListener('click', () => { currentFilter = button.dataset.cultivoFilter || 'todos'; filters.forEach(item => item.classList.remove('active')); button.classList.add('active'); applyFilters(); }));
        if (search) search.addEventListener('input', applyFilters);
        applyFilters();
    }

    showFlashNotifications();
    animateCards();
    startDashboardLive();
    setupCultivoFilters();
    setupStatsCropFilter();
    setupRiegoPanel();
    drawStatsCharts();
})();
