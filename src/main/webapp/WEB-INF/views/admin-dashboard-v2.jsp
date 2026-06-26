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
<main class="layout dashboard-grid">
    <section class="page-hero wide">
        <div>
            <p class="eyebrow">Admin Control Center</p>
            <h1>Operations, billing, staff and residents in one place.</h1>
            <p>Publish announcements, assign work with staff IDs, generate targeted bills, and inspect every workflow from dashboard detail panels.</p>
        </div>
    </section>

    <section class="panel wide">
        <div class="section-heading"><span class="section-icon">A</span><div><h2>Community Announcements</h2><p>Messages posted here are visible to residents and maintenance staff.</p></div></div>
        <form method="post" action="${pageContext.request.contextPath}/admin/announcements" class="grid-form">
            <label>Announcement title<input name="title" required placeholder="Water tank cleaning on Sunday"></label>
            <label class="form-span">Announcement message<textarea name="message" required placeholder="Write the update everyone should see"></textarea></label>
            <button type="submit">Publish Announcement</button>
        </form>
        <div class="notice-list">
            <c:forEach items="${announcements}" var="announcement">
                <article class="notice-card"><strong>${announcement.title}</strong><p>${announcement.message}</p><small>Posted by ${announcement.createdBy}</small></article>
            </c:forEach>
        </div>
    </section>

    <section class="stats-row wide">
        <a class="stat-card clickable" href="#open-details"><span>Open Complaints</span><strong>${stats['Open Complaints']}</strong><small>Click for details</small></a>
        <a class="stat-card clickable" href="#completed-details"><span>Completed Complaints</span><strong>${stats['Completed Complaints']}</strong><small>Recently completed work</small></a>
        <a class="stat-card clickable" href="#visitor-details"><span>Pending Visitors</span><strong>${stats['Pending Visitors']}</strong><small>Awaiting approval</small></a>
        <a class="stat-card clickable" href="#payment-details"><span>Due Payments</span><strong>${stats['Due Payments']}</strong><small>Unpaid bills</small></a>
        <a class="stat-card clickable" href="#flat-details"><span>Registered Flats</span><strong>${stats['Registered Flats']}</strong><small>Flat database</small></a>
    </section>

    <section class="analytics-grid wide">
        <article class="panel collection-card">
            <div class="section-heading"><span class="section-icon">₹</span><div><h2>Collection Overview</h2><p>Premium dashboard view for paid, due and processing invoices.</p></div></div>
            <div class="collection-layout">
                <div class="collection-metric danger"><span>Outstanding</span><strong>Rs. ${paymentAnalytics.dueAmount}</strong><small>${paymentAnalytics.dueCount} unpaid bills</small></div>
                <div class="big-ring" style="background: ${paymentAnalytics.ringStyle}"><span>June<br>2026</span></div>
                <div class="collection-metric success"><span>Collected</span><strong>Rs. ${paymentAnalytics.paidAmount}</strong><small>${paymentAnalytics.paidCount} paid bills</small></div>
            </div>
            <div class="avatar-stack">
                <span>A-101</span><span>B-204</span><span>C-302</span><span>+${stats['Registered Flats']}</span>
            </div>
        </article>
        <article class="panel occupancy-card">
            <div class="section-heading"><span class="section-icon">O</span><div><h2>Occupancy Statistics</h2><p>Flat database snapshot for admin review.</p></div></div>
            <div class="occupancy-row"><div><strong>${occupancyAnalytics.occupied}</strong><span>Occupied</span></div><div><strong>${occupancyAnalytics.vacant}</strong><span>Vacant</span></div><div class="mini-ring" style="background: ${occupancyAnalytics.ringStyle}"></div></div>
        </article>
        <article class="panel maintenance-chart">
            <div class="section-heading"><span class="section-icon">M</span><div><h2>Open Maintenance Request</h2><p>Graphical complaint status overview.</p></div></div>
            <div class="request-counts"><div><strong>${complaintStatusAnalytics.open}</strong><span>Open Request</span></div><div><strong>${complaintStatusAnalytics.completed}</strong><span>Completed</span></div></div>
            <div class="bar-chart" aria-label="Maintenance category chart">
                <c:forEach items="${complaintCategoryAnalytics}" var="cat">
                    <span style="height:${cat.height}%" title="${cat.category}: ${cat.count}"></span>
                </c:forEach>
            </div>
            <div class="chart-labels">
                <c:forEach items="${complaintCategoryAnalytics}" var="cat">
                    <span>${cat.category}</span>
                </c:forEach>
            </div>
        </article>
    </section>

    <section class="panel wide income-board">
        <div class="section-heading"><span class="section-icon">I</span><div><h2>Income & Invoice Statistics</h2><p>Innago-style invoice table with quick filters, payment status, balances and collection summary.</p></div></div>
        <div class="income-layout">
            <div class="income-main">
                <div class="filter-bar">
                    <button type="button" class="ghost-button">Filters</button>
                    <select><option>Not Grouped</option><option>Grouped by Flat</option><option>Grouped by Status</option></select>
                    <a class="export-link" href="#payment-details">Export</a>
                </div>
                <div class="showing-row"><strong>Showing</strong><span>${paymentAnalytics.dueCount} due invoices</span></div>
                <div class="table-wrap invoice-table"><table>
                    <thead><tr><th>Property/Shared By</th><th>Due On</th><th>Paid On</th><th>ID</th><th>Status</th><th>Unit</th><th>Amount</th><th>Processing</th><th>Paid</th><th>Balance</th></tr></thead>
                    <tbody>
                    <c:forEach items="${payments}" var="payment">
                        <tr>
                            <td data-label="Property/Shared By"><div class="property-cell"><span class="avatar-dot">${payment.flatNumber}</span><strong>${payment.flatNumber}</strong><small>${payment.description}</small></div></td>
                            <td data-label="Due On">${payment.month}-01</td>
                            <td data-label="Paid On">${payment.paidOn}</td>
                            <td data-label="ID">INV-${payment.id}</td>
                            <td data-label="Status"><span class="status status-${payment.status}">${payment.status}</span></td>
                            <td data-label="Unit">${payment.flatNumber}</td>
                            <td data-label="Amount">Rs. ${payment.amount}</td>
                            <td data-label="Processing">Rs. 0.00</td>
                            <td data-label="Paid"><c:choose><c:when test="${payment.status == 'PAID'}">Rs. ${payment.amount}</c:when><c:otherwise>Rs. 0.00</c:otherwise></c:choose></td>
                            <td data-label="Balance"><c:choose><c:when test="${payment.status == 'DUE'}">Rs. ${payment.amount}</c:when><c:otherwise>Rs. 0.00</c:otherwise></c:choose></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table></div>
            </div>
            <aside class="income-summary">
                <button type="button">New Invoice</button>
                <div class="donut-card">
                    <div class="donut" style="background: ${paymentAnalytics.ringStyle}"></div>
                    <strong>Collection Status</strong>
                    <span>Due vs Paid</span>
                </div>
                <div class="quick-filter-card overdue"><strong>Rs. ${paymentAnalytics.dueAmount}</strong><span>Overdue / Due (${paymentAnalytics.dueCount} bills)</span></div>
                <div class="quick-filter-card paid"><strong>Rs. ${paymentAnalytics.paidAmount}</strong><span>Total Paid (${paymentAnalytics.paidCount} bills)</span></div>
                <div class="quick-filter-card review"><strong>${stats['Pending Visitors']}</strong><span>Visitors For Review</span></div>
                <div class="quick-filter-card pending"><strong>${stats['Open Complaints']}</strong><span>Open Maintenance Tasks</span></div>
            </aside>
        </div>
    </section>

    <section class="panel action-panel">
        <div class="section-heading"><span class="section-icon">F</span><div><h2>Flat Management</h2><p>Add new apartments before assigning residents or bills.</p></div></div>
        <form method="post" action="${pageContext.request.contextPath}/admin/flats" class="grid-form">
            <label>Flat number<input name="flatNumber" required placeholder="A-103"></label>
            <label>Tower<input name="tower" required placeholder="A"></label>
            <label>Floor<input type="number" name="floorNumber" required placeholder="1"></label>
            <button type="submit">Add Flat</button>
        </form>
    </section>

    <section class="panel action-panel">
        <div class="section-heading"><span class="section-icon">Rs</span><div><h2>Generate Bill</h2><p>Create bills for everyone or only one selected flat.</p></div></div>
        <form method="post" action="${pageContext.request.contextPath}/admin/billing" class="grid-form">
            <label>Billing target<select name="flatId"><option value="ALL">Everyone</option><c:forEach items="${flats}" var="flat"><option value="${flat.id}">${flat.flatNumber} - Tower ${flat.tower}</option></c:forEach></select></label>
            <label>Billing month<input name="dueMonth" required placeholder="2026-06"></label>
            <label>Amount<input type="number" step="0.01" name="amount" required placeholder="3500"></label>
            <label>Description<select name="descriptionType"><option value="MAINTENANCE">Monthly maintenance</option><option value="CHARGES">Additional charges</option><option value="REPAIR">Repair charges</option><option value="OTHER">Other custom description</option></select></label>
            <label class="form-span">Custom description<input name="customDescription" placeholder="Only needed when Other is selected"></label>
            <button type="submit">Generate Bill</button>
        </form>
    </section>

    <section id="complaints" class="panel wide">
        <div class="section-heading"><span class="section-icon">C</span><div><h2>Complaint Assignment</h2><p>Assign only maintenance staff. The dropdown shows both staff name and ID.</p></div></div>
        <div class="table-wrap"><table>
            <thead><tr><th>Photo</th><th>Flat</th><th>Resident</th><th>Issue</th><th>Description</th><th>Status</th><th>Assign Staff</th></tr></thead>
            <tbody><c:forEach items="${complaints}" var="complaint"><tr>
                <td data-label="Photo">
                    <div style="display: flex; gap: 6px; flex-wrap: wrap;">
                        <c:choose>
                            <c:when test="${not empty complaint.photoUrl}">
                                <img class="thumb" src="${complaint.photoUrl}" alt="Complaint photo" title="Resident attached photo">
                            </c:when>
                            <c:otherwise>
                                <span class="empty-photo">No photo</span>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${not empty complaint.completionPhotoUrl}">
                            <img class="thumb" src="${complaint.completionPhotoUrl}" alt="Completion photo" title="Staff completion proof" style="border-color: var(--success);">
                        </c:if>
                    </div>
                </td>
                <td data-label="Flat"><strong>${complaint.flatNumber}</strong></td>
                <td data-label="Resident">${complaint.residentName}</td>
                <td data-label="Issue">${complaint.title}</td>
                <td data-label="Description">${complaint.description}</td>
                <td data-label="Status"><span class="status status-${complaint.status}">${complaint.status}</span></td>
                <td data-label="Assign Staff"><form method="post" action="${pageContext.request.contextPath}/admin/complaints/assign" class="inline-form compact-form"><input type="hidden" name="complaintId" value="${complaint.id}"><select name="staffId" required><c:forEach items="${staffUsers}" var="staff"><option value="${staff.id}">${staff.name} - ID ${staff.id}</option></c:forEach></select><button type="submit" class="small">Assign</button></form></td>
            </tr></c:forEach></tbody>
        </table></div>
    </section>

    <section id="visitors" class="panel wide">
        <div class="section-heading"><span class="section-icon">V</span><div><h2>Visitor Requests</h2><p>Approve or reject visitor entries with a message for the resident.</p></div></div>
        <div class="table-wrap"><table>
            <thead><tr><th>Flat</th><th>Visitor</th><th>Purpose</th><th>Status</th><th>Admin Note</th><th>Decision</th></tr></thead>
            <tbody><c:forEach items="${visitors}" var="visitor"><tr>
                <td data-label="Flat"><strong>${visitor.flatNumber}</strong></td>
                <td data-label="Visitor">${visitor.visitorName}</td>
                <td data-label="Purpose">${visitor.purpose}</td>
                <td data-label="Status"><span class="status status-${visitor.status}">${visitor.status}</span></td>
                <td data-label="Admin Note">${visitor.decisionNote}</td>
                <td data-label="Decision"><form method="post" action="${pageContext.request.contextPath}/admin/visitors/decide" class="inline-form compact-form"><input type="hidden" name="requestId" value="${visitor.id}"><select name="status"><option value="APPROVED">Approve</option><option value="REJECTED">Reject</option></select><input name="decisionNote" placeholder="Reason or gate instruction"><button type="submit" class="small">Save</button></form></td>
            </tr></c:forEach></tbody>
        </table></div>
    </section>

    <section id="working-staff" class="panel wide"><div class="section-heading"><span class="section-icon">W</span><div><h2>Staff Currently Working</h2><p>Assigned and in-progress tasks show which staff member is handling the work.</p></div></div><div class="mini-list"><c:forEach items="${workingStaffDetails}" var="row"><article class="mini-card"><strong>${row.title}</strong><span>${row.flat_number} - ${row.status}</span><small>${row.staff} - ID ${row.staff_id}</small></article></c:forEach></div></section>
    <section id="open-details" class="panel wide detail-panel"><h2>Open Complaint Details</h2><div class="mini-list"><c:forEach items="${openComplaintDetails}" var="row"><article class="mini-card"><strong>${row.title}</strong><span>${row.flat_number} - ${row.resident} - ${row.status}</span><small>${row.description}</small></article></c:forEach></div></section>
    <section id="completed-details" class="panel wide detail-panel"><h2>Recently Completed Work</h2><div class="mini-list"><c:forEach items="${completedComplaintDetails}" var="row"><article class="mini-card"><strong>${row.title}</strong><span>${row.flat_number} - ${row.staff}</span><small>${row.description}</small></article></c:forEach></div></section>
    <section id="visitor-details" class="panel wide detail-panel"><h2>Pending Visitor Details</h2><div class="mini-list"><c:forEach items="${pendingVisitorDetails}" var="row"><article class="mini-card"><strong>${row.visitor_name}</strong><span>${row.flat_number} - ${row.resident}</span><small>${row.purpose}</small></article></c:forEach></div></section>
    <section id="payment-details" class="panel wide detail-panel"><h2>Due Payment Details</h2><div class="mini-list"><c:forEach items="${duePaymentDetails}" var="row"><article class="mini-card"><strong>${row.flat_number} - Rs. ${row.amount}</strong><span>${row.due_month} - ${row.status}</span><small>${row.description}</small></article></c:forEach></div></section>
    <section id="flat-details" class="panel wide detail-panel"><h2>Registered Flats Database</h2><div class="mini-list"><c:forEach items="${flatDetails}" var="row"><article class="mini-card"><strong>${row.flat_number}</strong><span>Tower ${row.tower}, Floor ${row.floor_number}</span><small>${row.resident}</small></article></c:forEach></div></section>
</main>
</body>
</html>
