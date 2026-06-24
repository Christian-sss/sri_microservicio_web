<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro - SRI Web</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="/css/app.css">
</head>
<body class="login-page">
    <section class="login-card">
        <div class="login-brand"><div class="brand-icon"><i class="fa-solid fa-user-plus"></i></div><div><h1>Registrar usuario</h1><span>Crea una cuenta para acceder al panel</span></div></div>
        <jsp:include page="/WEB-INF/jsp/components/messages.jsp" />
        <form action="/registro" method="post" class="stack-form">
            <label>Correo o usuario<input type="text" name="username" value="${username}" autocomplete="username" required autofocus></label>
            <label>Contrasena<input type="password" name="password" autocomplete="new-password" minlength="4" required></label>
            <label>Confirmar contrasena<input type="password" name="confirmPassword" autocomplete="new-password" minlength="4" required></label>
            <div class="captcha-box">
                <div class="captcha-header">
                    <span><i class="fa-solid fa-shield-halved"></i> Verificacion</span>
                    <small>Ingresa el codigo</small>
                </div>
                <div class="captcha-row">
                    <div class="captcha-code">
                        <img src="${captchaImage}" alt="Captcha de letras">
                    </div>
                    <label class="captcha-input">Codigo
                        <input type="text" name="captcha" maxlength="5" autocomplete="off" required>
                    </label>
                </div>
            </div>
            <button type="submit" class="btn"><i class="fa-solid fa-user-plus"></i> Crear cuenta</button>
        </form>
        <p class="auth-switch">Ya tienes cuenta? <a href="/login">Iniciar sesion</a></p>
    </section>
</body>
</html>
