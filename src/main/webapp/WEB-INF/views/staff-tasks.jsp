<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Staff Tasks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/premium.css">
</head>
<body>
<jsp:include page="partials/app-header.jsp"/>
<main class="layout single">
    <section class="page-hero wide">
        <div>
            <p class="eyebrow">Maintenance Staff</p>
            <h1>Update assigned work clearly and quickly.</h1>
            <p>Track complaints assigned to you, change progress, and attach completion proof for the resident and admin.</p>
        </div>
    </section>

    <section class="panel wide">
        <div class="section-heading">
            <span class="section-icon">W</span>
            <div>
                <h2>Assigned Work</h2>
                <p>Use the update form when a task moves forward or is completed.</p>
            </div>
        </div>
        <div class="table-wrap">
            <table>
            <thead><tr><th>Flat</th><th>Issue</th><th>Status</th><th>Completion Update</th></tr></thead>
            <tbody>
            <c:forEach items="${tasks}" var="task">
                <tr>
                    <td data-label="Flat"><strong>${task.flatNumber}</strong></td>
                    <td data-label="Issue"><strong>${task.title}</strong><span class="muted-line">${task.description}</span></td>
                    <td data-label="Status"><span class="status">${task.status}</span></td>
                    <td data-label="Completion Update">
                        <form method="post" action="${pageContext.request.contextPath}/staff/tasks" class="inline-form">
                            <input type="hidden" name="complaintId" value="${task.id}">
                            <select name="status">
                                <option value="IN_PROGRESS">In progress</option>
                                <option value="COMPLETED">Completed</option>
                            </select>
                            <input name="completionPhotoUrl" placeholder="Completion photo URL">
                            <button type="submit" class="small">Update</button>
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
