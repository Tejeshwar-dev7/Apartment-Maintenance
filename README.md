# Apartment Maintenance Management System

A resume-ready Java web application for apartment operations. It replaces scattered WhatsApp messages with role-based workflows for complaints, visitors and maintenance payments.

## Tech Stack

- Java 17
- Jakarta Servlets and JSP
- JDBC
- MySQL
- Maven WAR packaging
- Tomcat 10+

## Features

### Resident

- Raise complaints for water, lift, electrical and cleaning issues
- Track complaint status and assigned maintenance staff
- Request visitor approval
- Pay monthly maintenance dues
- Download payment receipts

### Maintenance Staff

- View assigned complaints
- Update status to in progress or completed
- Attach completion photo URL

### Admin

- View dashboard metrics
- Add flats
- Generate monthly maintenance bills
- Assign complaints to staff
- Approve or reject visitor requests
- Review complaints, visitors and payments

## Database Setup

1. Create the database and seed demo data:

```sql
SOURCE database/schema.sql;
```

2. Copy the example database config:

```powershell
Copy-Item src/main/resources/db.properties.example src/main/resources/db.properties
```

3. Update `src/main/resources/db.properties` with your MySQL username and password.

## Run Locally

1. Build the WAR:

```powershell
mvn clean package
```

2. Deploy `target/apartment-maintenance.war` to Tomcat 10 or newer.

3. Open:

```text
http://localhost:8080/apartment-maintenance
```

## Demo Accounts

All demo users use `password123`.

| Role | Email |
| --- | --- |
| Admin | admin@ams.com |
| Resident | resident@ams.com |
| Staff | staff@ams.com |

## Resume Description

Built an Apartment Maintenance Management System using Java Servlets, JDBC and MySQL with role-based access for residents, maintenance staff and admins. Implemented complaint tracking, staff assignment, visitor approvals, monthly billing, payment receipts and admin dashboard reporting.

## Suggested Interview Talking Points

- Used servlet filters for role-based authorization.
- Used DAO classes and prepared statements to keep SQL access organized and safe.
- Modeled real workflows with statuses: complaint lifecycle, visitor decisions and payment states.
- Added MySQL constraints for unique monthly bills per flat.
- Separated controllers, models, DAOs and JSP views for maintainable MVC-style structure.
