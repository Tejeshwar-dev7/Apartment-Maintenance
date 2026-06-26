<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="topbar">
    <a class="brand" href="${pageContext.request.contextPath}/login">
        <span class="brand-mark">AM</span>
        <span>
            <strong>ApartmentCare</strong>
            <small>Maintenance Management</small>
        </span>
    </a>
    <nav class="nav-links" aria-label="Primary navigation">
        <c:choose>
            <c:when test="${sessionScope.user.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/dashboard"><span>⌂</span>Dashboard</a>
                <a href="#complaints"><span>✚</span>Complaints</a>
                <a href="#visitors"><span>☑</span>Visitors</a>
            </c:when>
            <c:when test="${sessionScope.user.role == 'STAFF'}">
                <a href="${pageContext.request.contextPath}/staff/tasks"><span>⚒</span>Work</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/resident/dashboard"><span>⌂</span>Dashboard</a>
                <a href="#complaints"><span>✚</span>Complaints</a>
                <a href="#payments"><span>₹</span>Payments</a>
            </c:otherwise>
        </c:choose>
    </nav>
    <div class="user-chip">
        <span>${sessionScope.user.name}</span>
        <small>${sessionScope.user.role}</small>
    </div>
    <a class="logout-link" href="${pageContext.request.contextPath}/logout">Logout</a>
</header>
