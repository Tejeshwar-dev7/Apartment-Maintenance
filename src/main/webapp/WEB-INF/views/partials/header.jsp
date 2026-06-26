<%@ page contentType="text/html;charset=UTF-8" %>
<header class="topbar">
    <div>
        <strong>Apartment Maintenance</strong>
        <span>${sessionScope.user.name} · ${sessionScope.user.role}</span>
    </div>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</header>
