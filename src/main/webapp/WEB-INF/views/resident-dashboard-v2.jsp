<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Resident Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/premium.css">
</head>
<body>
<jsp:include page="partials/app-header.jsp"/>
<main class="layout">
    <section class="page-hero wide"><div><p class="eyebrow">Resident Portal</p><h1>Your flat, visitors, payments and complaints in one view.</h1><p>Announcements from admin appear here for everyone, and every request keeps its status visible.</p></div></section>

    <section class="panel wide"><div class="section-heading"><span class="section-icon">A</span><div><h2>Announcements</h2><p>Common updates shared by the apartment admin.</p></div></div><div class="notice-list"><c:forEach items="${announcements}" var="announcement"><article class="notice-card"><strong>${announcement.title}</strong><p>${announcement.message}</p><small>Posted by ${announcement.createdBy}</small></article></c:forEach></div></section>

    <section class="panel action-panel wide">
        <div class="section-heading"><span class="section-icon">!</span><div><h2>Raise Complaint</h2><p>Use your own complaint title and describe the problem clearly.</p></div></div>
        <div class="ticket-shell">
            <form method="post" action="${pageContext.request.contextPath}/resident/complaints" enctype="multipart/form-data" class="grid-form">
                <label class="form-span">Complaint title<input name="title" required placeholder="Kitchen tap leakage"></label>
                <div class="category-grid">
                    <label class="category-card"><input type="radio" name="category" value="Electrical" required><span>Electrical</span></label>
                    <label class="category-card"><input type="radio" name="category" value="Water"><span>Water / Plumbing</span></label>
                    <label class="category-card"><input type="radio" name="category" value="Lift"><span>Lift</span></label>
                    <label class="category-card"><input type="radio" name="category" value="Cleaning"><span>Cleaning</span></label>
                    <label class="category-card"><input type="radio" name="category" value="Repair"><span>Repair</span></label>
                    <label class="category-card"><input type="radio" name="category" value="Other"><span>Other</span></label>
                </div>
                <label class="form-span">Problem description<textarea name="description" required placeholder="Write what is happening, location, and urgency"></textarea></label>
                <label class="form-span">Attach complaint photo<input type="file" name="photo" accept="image/*"></label>
                <button type="submit">Submit Complaint</button>
            </form>
            <aside class="upload-preview">
                <div>
                    <strong>Attach Photo</strong>
                    <p>Upload leakage, lift issue, repair damage or cleaning proof.</p>
                    <span>Visible to admin and assigned staff</span>
                </div>
            </aside>
        </div>
    </section>

    <section class="panel action-panel">
        <div class="section-heading"><span class="section-icon">V</span><div><h2>Visitor Approval</h2><p>Track whether admin approved or rejected the visitor request.</p></div></div>
        <form method="post" action="${pageContext.request.contextPath}/resident/visitors" class="grid-form">
            <label>Visitor name<input name="visitorName" required placeholder="Meera Kapoor"></label>
            <label>Phone<input name="phone" required placeholder="9876543210"></label>
            <label>Purpose<input name="purpose" required placeholder="Family visit"></label>
            <label>Visit time<input type="datetime-local" name="visitTime" required></label>
            <button type="submit">Request Approval</button>
        </form>
    </section>

    <section id="complaints" class="panel wide"><div class="section-heading"><span class="section-icon">C</span><div><h2>Complaint Tracking</h2><p>Photo, title, description and status stay visible in the complaint row.</p></div></div><div class="table-wrap"><table><thead><tr><th>Photo</th><th>Title</th><th>Description</th><th>Category</th><th>Status</th><th>Staff</th></tr></thead><tbody><c:forEach items="${complaints}" var="complaint"><tr><td data-label="Photo"><c:choose><c:when test="${not empty complaint.photoUrl}"><img class="thumb" src="${complaint.photoUrl}" alt="Complaint photo"></c:when><c:otherwise><span class="empty-photo">No photo</span></c:otherwise></c:choose></td><td data-label="Title"><strong>${complaint.title}</strong></td><td data-label="Description">${complaint.description}</td><td data-label="Category">${complaint.category}</td><td data-label="Status"><span class="status status-${complaint.status}">${complaint.status}</span></td><td data-label="Staff">${complaint.assignedStaffName}</td></tr></c:forEach></tbody></table></div></section>

    <section id="visitors" class="panel wide"><div class="section-heading"><span class="section-icon">V</span><div><h2>Visitor Status</h2><p>Approved or rejected status appears here with the admin note.</p></div></div><div class="table-wrap"><table><thead><tr><th>Visitor</th><th>Purpose</th><th>Visit Time</th><th>Status</th><th>Admin Note</th></tr></thead><tbody><c:forEach items="${visitors}" var="visitor"><tr><td data-label="Visitor"><strong>${visitor.visitorName}</strong></td><td data-label="Purpose">${visitor.purpose}</td><td data-label="Visit Time">${visitor.visitTime}</td><td data-label="Status"><span class="status status-${visitor.status}">${visitor.status}</span></td><td data-label="Admin Note">${visitor.decisionNote}</td></tr></c:forEach></tbody></table></div></section>

    <section id="payments" class="panel wide"><div class="section-heading"><span class="section-icon">Rs</span><div><h2>Maintenance Payments</h2><p>Bill descriptions explain whether it is maintenance, repair, charges or custom.</p></div></div><div class="table-wrap"><table><thead><tr><th>Month</th><th>Description</th><th>Amount</th><th>Status</th><th>Action</th></tr></thead><tbody><c:forEach items="${payments}" var="payment"><tr><td data-label="Month"><strong>${payment.month}</strong></td><td data-label="Description">${payment.description}</td><td data-label="Amount">Rs. ${payment.amount}</td><td data-label="Status"><span class="status status-${payment.status}">${payment.status}</span></td><td data-label="Action"><c:choose><c:when test="${payment.status == 'DUE'}"><form method="post" action="${pageContext.request.contextPath}/resident/payments/pay"><input type="hidden" name="paymentId" value="${payment.id}"><button type="submit" class="small">Pay Now</button></form></c:when><c:otherwise><a class="button-link" href="${pageContext.request.contextPath}/resident/payments/receipt?paymentId=${payment.id}">Download Receipt</a></c:otherwise></c:choose></td></tr></c:forEach></tbody></table></div></section>
</main>
</body>
</html>
