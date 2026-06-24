<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - SRI Web</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="/css/app.css">
</head>
<body class="login-page">
    <section class="login-card">
        <div class="login-brand"><div class="brand-icon"><i class="fa-solid fa-leaf"></i></div><div><h1>SRI Web</h1><span>No se pudo completar la solicitud</span></div></div>
        <div class="alert error"><i class="fa-solid fa-triangle-exclamation"></i><span>${message}</span></div>
        <a class="btn secondary" href="/dashboard"><i class="fa-solid fa-arrow-left"></i> Volver</a>
    </section>
</body>
</html>
