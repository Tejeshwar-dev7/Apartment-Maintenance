package com.apartment.servlet;

import com.apartment.dao.AnnouncementDao;
import com.apartment.dao.AdminDao;
import com.apartment.dao.ComplaintDao;
import com.apartment.dao.LookupDao;
import com.apartment.dao.PaymentDao;
import com.apartment.dao.VisitorDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private final AnnouncementDao announcementDao = new AnnouncementDao();
    private final AdminDao adminDao = new AdminDao();
    private final ComplaintDao complaintDao = new ComplaintDao();
    private final LookupDao lookupDao = new LookupDao();
    private final VisitorDao visitorDao = new VisitorDao();
    private final PaymentDao paymentDao = new PaymentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("announcements", announcementDao.findLatest());
            request.setAttribute("stats", adminDao.dashboardStats());
            request.setAttribute("complaints", complaintDao.findAll());
            request.setAttribute("visitors", visitorDao.findAll());
            request.setAttribute("payments", paymentDao.findAll());
            request.setAttribute("staffUsers", lookupDao.findStaffUsers());
            request.setAttribute("flats", lookupDao.findFlats());
            request.setAttribute("openComplaintDetails", adminDao.openComplaintDetails());
            request.setAttribute("completedComplaintDetails", adminDao.completedComplaintDetails());
            request.setAttribute("pendingVisitorDetails", adminDao.pendingVisitorDetails());
            request.setAttribute("duePaymentDetails", adminDao.duePaymentDetails());
            request.setAttribute("flatDetails", adminDao.flatDetails());
            request.setAttribute("workingStaffDetails", adminDao.workingStaffDetails());
            request.getRequestDispatcher("/WEB-INF/views/admin-dashboard-v2.jsp").forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }
}
