# Apartment Maintenance Management System

A resume-ready Java web application for apartment operations. It replaces scattered WhatsApp messages with role-based workflows for complaints, visitors and maintenance payments.

## Live Website

This project is ready to deploy for free on Render using the included `Dockerfile` and `render.yaml`.

After deploying, add your Render URL here:

```text
https://your-render-service.onrender.com
```

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

## Deploy Free on Render

1. Push this repository to GitHub.
2. Create a free MySQL database from any MySQL-compatible provider.
3. Import `database/schema.sql` into that database.
4. Go to Render, choose **New +** -> **Blueprint**, and connect this GitHub repository.
5. Render will detect `render.yaml` and create the `apartment-maintenance` web service.
6. Add these environment variables in Render:

```text
DB_URL=jdbc:mysql://YOUR_HOST:3306/YOUR_DATABASE?useSSL=true&serverTimezone=UTC
DB_USERNAME=YOUR_DATABASE_USER
DB_PASSWORD=YOUR_DATABASE_PASSWORD
```

7. Deploy the service and open the Render URL.
8. In GitHub, open repository **Settings** -> **General** -> **About** and paste the Render URL into **Website**.

The Docker deployment publishes the app at the root path, so the live site opens directly from the Render URL.

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
