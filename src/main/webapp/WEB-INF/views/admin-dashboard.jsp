<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/premium.css">
</head>
<body>
<jsp:include page="partials/app-header.jsp"/>
<main class="layout">
    <section class="page-hero wide">
        <div>
            <p class="eyebrow">Admin Control Center</p>
            <h1>Manage flats, complaints, visitors and monthly billing.</h1>
            <p>Use this dashboard to keep operations structured and easy to audit.</p>
        </div>
    </section>

    <section class="stats-row wide">
        <c:forEach items="${stats}" var="stat">
            <article class="stat-card">
                <span>${stat.key}</span>
                <strong>${stat.value}</strong>
            </article>
        </c:forEach>
    </section>

    <section class="panel action-panel">
        <div class="section-heading">
            <span class="section-icon">F</span>
            <div>
                <h2>Flat Management</h2>
                <p>Add new apartments before assigning residents or bills.</p>
            </div>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/admin/flats" class="grid-form">
            <label>Flat number
                <input name="flatNumber" required placeholder="A-103">
            </label>
            <label>Tower
                <input name="tower" required placeholder="A">
            </label>
            <label>Floor
                <input type="number" name="floorNumber" required placeholder="1">
            </label>
            <button type="submit">Add Flat</button>
        </form>
    </section>

    <section class="panel action-panel">
        <div class="section-heading">
            <span class="section-icon">Rs</span>
            <div>
                <h2>Monthly Maintenance</h2>
                <p>Generate one bill per flat for a selected month.</p>
            </div>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/admin/billing" class="grid-form">
            <label>Billing month
                <input name="dueMonth" required placeholder="2026-06">
            </label>
            <label>Amount
                <input type="number" step="0.01" name="amount" required placeholder="3500">
            </label>
            <button type="submit">Generate Bills</button>
        </form>
    </section>

    <section id="complaints" class="panel wide">
        <div class="section-heading">
            <span class="section-icon">C</span>
            <div>
                <h2>Complaint Assignment</h2>
                <p>Review resident issues and assign staff by staff user ID.</p>
            </div>
        </div>
        <div class="table-wrap">
            <table>
            <thead><tr><th>Flat</th><th>Resident</th><th>Issue</th><th>Status</th><th>Assign Staff ID</th></tr></thead>
            <tbody>
            <c:forEach items="${complaints}" var="complaint">
                <tr>
                    <td data-label="Flat"><strong>${complaint.flatNumber}</strong></td>
                    <td data-label="Resident">${complaint.residentName}</td>
                    <td data-label="Issue">${complaint.title}</td>
                    <td data-label="Status"><span class="status">${complaint.status}</span></td>
                    <td data-label="Assign Staff ID">
                        <form method="post" action="${pageContext.request.contextPath}/admin/complaints/assign" class="inline-form">
                            <input type="hidden" name="complaintId" value="${complaint.id}">
                            <input type="number" name="staffId" required placeholder="Staff ID">
                            <button type="submit" class="small">Assign</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
            </table>
        </div>
    </section>

    <section id="visitors" class="panel wide">
        <div class="section-heading">
            <span class="section-icon">V</span>
            <div>
                <h2>Visitor Requests</h2>
                <p>Approve or reject visitor entries before arrival.</p>
            </div>
        </div>
        <div class="table-wrap">
            <table>
            <thead><tr><th>Flat</th><th>Visitor</th><th>Purpose</th><th>Status</th><th>Decision</th></tr></thead>
            <tbody>
            <c:forEach items="${visitors}" var="visitor">
                <tr>
                    <td data-label="Flat"><strong>${visitor.flatNumber}</strong></td>
                    <td data-label="Visitor">${visitor.visitorName}</td>
                    <td data-label="Purpose">${visitor.purpose}</td>
                    <td data-label="Status"><span class="status">${visitor.status}</span></td>
                    <td data-label="Decision">
                        <form method="post" action="${pageContext.request.contextPath}/admin/visitors/decide" class="inline-form">
                            <input type="hidden" name="requestId" value="${visitor.id}">
                            <select name="status">
                                <option value="APPROVED">Approve</option>
                                <option value="REJECTED">Reject</option>
                            </select>
                            <button type="submit" class="small">Save</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
            </table>
        </div>
    </section>
</main>
</body>
</html>
