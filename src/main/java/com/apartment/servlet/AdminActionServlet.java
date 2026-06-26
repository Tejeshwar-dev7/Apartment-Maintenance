package com.apartment.servlet;

import com.apartment.dao.AnnouncementDao;
import com.apartment.dao.AdminDao;
import com.apartment.dao.ComplaintDao;
import com.apartment.dao.PaymentDao;
import com.apartment.dao.VisitorDao;
import com.apartment.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/admin/flats", "/admin/billing", "/admin/complaints/assign", "/admin/visitors/decide", "/admin/announcements"})
public class AdminActionServlet extends HttpServlet {
    private final AnnouncementDao announcementDao = new AnnouncementDao();
    private final AdminDao adminDao = new AdminDao();
    private final ComplaintDao complaintDao = new ComplaintDao();
    private final PaymentDao paymentDao = new PaymentDao();
    private final VisitorDao visitorDao = new VisitorDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getServletPath();
            if ("/admin/flats".equals(path)) {
                adminDao.addFlat(
                        request.getParameter("flatNumber"),
                        request.getParameter("tower"),
                        Integer.parseInt(request.getParameter("floorNumber"))
                );
            } else if ("/admin/billing".equals(path)) {
                paymentDao.generateMonthlyBills(
                        request.getParameter("dueMonth"),
                        new BigDecimal(request.getParameter("amount")),
                        billDescription(request),
                        selectedFlatId(request.getParameter("flatId"))
                );
            } else if ("/admin/complaints/assign".equals(path)) {
                complaintDao.assign(
                        Integer.parseInt(request.getParameter("complaintId")),
                        Integer.parseInt(request.getParameter("staffId"))
                );
            } else if ("/admin/visitors/decide".equals(path)) {
                visitorDao.decide(
                        Integer.parseInt(request.getParameter("requestId")),
                        request.getParameter("status"),
                        request.getParameter("decisionNote")
                );
            } else if ("/admin/announcements".equals(path)) {
                User user = (User) request.getSession().getAttribute("user");
                announcementDao.create(request.getParameter("title"), request.getParameter("message"), user.getId());
            }
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }

    private String billDescription(HttpServletRequest request) {
        String type = request.getParameter("descriptionType");
        if ("OTHER".equals(type)) {
            String custom = request.getParameter("customDescription");
            return custom == null || custom.isBlank() ? "Other charges" : custom;
        }
        if ("REPAIR".equals(type)) {
            return "Repair charges";
        }
        if ("CHARGES".equals(type)) {
            return "Additional charges";
        }
        return "Monthly maintenance";
    }

    private Integer selectedFlatId(String flatId) {
        if (flatId == null || flatId.isBlank() || "ALL".equals(flatId)) {
            return null;
        }
        return Integer.parseInt(flatId);
    }
}
