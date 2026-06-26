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
    <section class="page-hero wide">
        <div>
            <p class="eyebrow">Resident Portal</p>
            <h1>Everything for your flat, in one calm dashboard.</h1>
            <p>Raise service issues, approve visitors, pay maintenance and download receipts from a single place.</p>
        </div>
    </section>

    <section class="panel action-panel">
        <div class="section-heading">
            <span class="section-icon">!</span>
            <div>
                <h2>Raise Complaint</h2>
                <p>Submit water, lift, electrical or cleaning issues.</p>
            </div>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/resident/complaints" class="grid-form">
            <label>Issue title
                <input name="title" required placeholder="Low water pressure">
            </label>
            <label>Category
                <select name="category" required>
                    <option value="Water">Water</option>
                    <option value="Lift">Lift</option>
                    <option value="Electrical">Electrical</option>
                    <option value="Cleaning">Cleaning</option>
                </select>
            </label>
            <label class="form-span">Description
                <textarea name="description" required placeholder="Describe what happened and where it is happening"></textarea>
            </label>
            <button type="submit">Submit Complaint</button>
        </form>
    </section>

    <section class="panel action-panel">
        <div class="section-heading">
            <span class="section-icon">V</span>
            <div>
                <h2>Visitor Approval</h2>
                <p>Create a visitor request before arrival.</p>
            </div>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/resident/visitors" class="grid-form">
            <label>Visitor name
                <input name="visitorName" required placeholder="Meera Kapoor">
            </label>
            <label>Phone
                <input name="phone" required placeholder="9876543210">
            </label>
            <label>Purpose
                <input name="purpose" required placeholder="Family visit">
            </label>
            <label>Visit time
                <input type="datetime-local" name="visitTime" required>
            </label>
            <button type="submit">Request Approval</button>
        </form>
    </section>

    <section id="complaints" class="panel wide">
        <div class="section-heading">
            <span class="section-icon">C</span>
            <div>
                <h2>Complaint Tracking</h2>
                <p>Follow each issue from raised to completed.</p>
            </div>
        </div>
        <div class="table-wrap">
            <table>
            <thead><tr><th>Title</th><th>Category</th><th>Status</th><th>Staff</th></tr></thead>
            <tbody>
            <c:forEach items="${complaints}" var="complaint">
                <tr>
                    <td data-label="Title"><strong>${complaint.title}</strong></td>
                    <td data-label="Category">${complaint.category}</td>
                    <td data-label="Status"><span class="status">${complaint.status}</span></td>
                    <td data-label="Staff">${complaint.assignedStaffName}</td>
                </tr>
            </c:forEach>
            </tbody>
            </table>
        </div>
    </section>

    <section id="payments" class="panel wide">
        <div class="section-heading">
            <span class="section-icon">Rs</span>
            <div>
                <h2>Maintenance Payments</h2>
                <p>Pay dues and download receipts after payment.</p>
            </div>
        </div>
        <div class="table-wrap">
            <table>
            <thead><tr><th>Month</th><th>Amount</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
            <c:forEach items="${payments}" var="payment">
                <tr>
                    <td data-label="Month"><strong>${payment.month}</strong></td>
                    <td data-label="Amount">Rs. ${payment.amount}</td>
                    <td data-label="Status"><span class="status">${payment.status}</span></td>
                    <td data-label="Action">
                        <c:choose>
                            <c:when test="${payment.status == 'DUE'}">
                                <form method="post" action="${pageContext.request.contextPath}/resident/payments/pay">
                                    <input type="hidden" name="paymentId" value="${payment.id}">
                                    <button type="submit" class="small">Pay Now</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <a class="button-link" href="${pageContext.request.contextPath}/resident/payments/receipt?paymentId=${payment.id}">Download Receipt</a>
                            </c:otherwise>
                        </c:choose>
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
