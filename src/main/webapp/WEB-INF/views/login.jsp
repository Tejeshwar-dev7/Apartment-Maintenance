<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Apartment Maintenance Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/premium.css">
</head>
<body class="login-page">
<main class="login-shell">
    <section class="login-panel">
        <div class="login-copy">
            <p class="eyebrow">ApartmentCare</p>
            <h1>Run apartment maintenance without WhatsApp chaos.</h1>
            <p>Complaints, visitor approvals, staff work updates, monthly bills and receipts stay organized for every flat.</p>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/login" class="form-card">
            <h2>Sign in</h2>
            <label>Email address
                <input type="email" name="email" required placeholder="resident@example.com">
            </label>
            <label>Password
                <input type="password" name="password" required placeholder="password">
            </label>
            <button type="submit">Sign in</button>
            <p class="error">${error}</p>
        </form>
        <div class="demo-users">
            <strong>Demo accounts</strong>
            <span>Admin: admin@ams.com / password123</span>
            <span>Resident: resident@ams.com / password123</span>
            <span>Staff: staff@ams.com / password123</span>
        </div>
    </section>
</main>
</body>
</html>
