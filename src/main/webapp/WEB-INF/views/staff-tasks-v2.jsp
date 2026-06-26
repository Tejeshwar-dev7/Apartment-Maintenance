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
    <section class="page-hero wide"><div><p class="eyebrow">Maintenance Staff</p><h1>Assigned work with complete resident problem details.</h1><p>Only staff users receive work. Update progress and attach completion proof once the job is done.</p></div></section>
    <section class="panel wide"><div class="section-heading"><span class="section-icon">A</span><div><h2>Announcements</h2><p>Common updates from admin for the full apartment team.</p></div></div><div class="notice-list"><c:forEach items="${announcements}" var="announcement"><article class="notice-card"><strong>${announcement.title}</strong><p>${announcement.message}</p><small>Posted by ${announcement.createdBy}</small></article></c:forEach></div></section>
    <section class="panel wide"><div class="section-heading"><span class="section-icon">W</span><div><h2>Assigned Work</h2><p>Read the full complaint description and attached resident photo before updating progress.</p></div></div><div class="table-wrap"><table><thead><tr><th>Photo</th><th>Flat</th><th>Complaint</th><th>Description</th><th>Status</th><th>Completion Update</th></tr></thead><tbody><c:forEach items="${tasks}" var="task"><tr><td data-label="Photo"><c:choose><c:when test="${not empty task.photoUrl}"><img class="thumb" src="${task.photoUrl}" alt="Complaint photo"></c:when><c:otherwise><span class="empty-photo">No photo</span></c:otherwise></c:choose></td><td data-label="Flat"><strong>${task.flatNumber}</strong></td><td data-label="Complaint"><strong>${task.title}</strong><span class="muted-line">${task.category}</span></td><td data-label="Description">${task.description}</td><td data-label="Status"><span class="status status-${task.status}">${task.status}</span></td><td data-label="Completion Update"><form method="post" action="${pageContext.request.contextPath}/staff/tasks" class="inline-form compact-form"><input type="hidden" name="complaintId" value="${task.id}"><select name="status"><option value="IN_PROGRESS">In progress</option><option value="COMPLETED">Completed</option></select><input name="completionPhotoUrl" placeholder="Completion photo URL"><button type="submit" class="small">Update</button></form></td></tr></c:forEach></tbody></table></div></section>
</main>
</body>
</html>
