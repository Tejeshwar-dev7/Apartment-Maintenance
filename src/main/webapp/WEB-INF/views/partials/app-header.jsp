<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<button class="mobile-nav-toggle" id="mobileNavToggle" aria-label="Toggle navigation">
    <span class="bar"></span>
    <span class="bar"></span>
    <span class="bar"></span>
</button>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<header class="sidebar" id="appSidebar">
    <a class="brand" href="${pageContext.request.contextPath}/login">
        <span class="brand-mark">AM</span>
        <div class="brand-text">
            <strong>ApartmentCare</strong>
            <small>Maintenance Management</small>
        </div>
    </a>
    <nav class="nav-links" aria-label="Primary navigation">
        <c:choose>
            <c:when test="${sessionScope.user.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item">
                    <span class="nav-icon">⌂</span>
                    <span class="nav-text">Dashboard</span>
                </a>
                <a href="${pageContext.request.contextPath}/admin/dashboard#complaints" class="nav-item">
                    <span class="nav-icon">✚</span>
                    <span class="nav-text">Complaints</span>
                </a>
                <a href="${pageContext.request.contextPath}/admin/dashboard#visitors" class="nav-item">
                    <span class="nav-icon">☑</span>
                    <span class="nav-text">Visitors</span>
                </a>
            </c:when>
            <c:when test="${sessionScope.user.role == 'STAFF'}">
                <a href="${pageContext.request.contextPath}/staff/tasks" class="nav-item">
                    <span class="nav-icon">⚒</span>
                    <span class="nav-text">Work Tasks</span>
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/resident/dashboard" class="nav-item">
                    <span class="nav-icon">⌂</span>
                    <span class="nav-text">Dashboard</span>
                </a>
                <a href="${pageContext.request.contextPath}/resident/dashboard#complaints" class="nav-item">
                    <span class="nav-icon">✚</span>
                    <span class="nav-text">Complaints</span>
                </a>
                <a href="${pageContext.request.contextPath}/resident/dashboard#payments" class="nav-item">
                    <span class="nav-icon">₹</span>
                    <span class="nav-text">Payments</span>
                </a>
            </c:otherwise>
        </c:choose>
    </nav>
    <div class="user-chip">
        <span class="user-name">${sessionScope.user.name}</span>
        <small class="user-role">${sessionScope.user.role}</small>
    </div>
    <a class="logout-link" href="${pageContext.request.contextPath}/logout">
        <span class="logout-icon">➔</span>
        <span class="logout-text">Logout</span>
    </a>
</header>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const toggle = document.getElementById('mobileNavToggle');
        const sidebar = document.getElementById('appSidebar');
        const overlay = document.getElementById('sidebarOverlay');
        if (toggle && sidebar && overlay) {
            toggle.addEventListener('click', function() {
                toggle.classList.toggle('open');
                sidebar.classList.toggle('open');
                overlay.classList.toggle('active');
            });
            overlay.addEventListener('click', function() {
                toggle.classList.remove('open');
                sidebar.classList.remove('open');
                overlay.classList.remove('active');
            });
        }
    });
</script>
