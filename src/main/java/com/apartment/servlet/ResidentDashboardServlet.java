package com.apartment.servlet;

import com.apartment.dao.AnnouncementDao;
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
import java.sql.SQLException;

@WebServlet("/resident/dashboard")
public class ResidentDashboardServlet extends HttpServlet {
    private final AnnouncementDao announcementDao = new AnnouncementDao();
    private final ComplaintDao complaintDao = new ComplaintDao();
    private final VisitorDao visitorDao = new VisitorDao();
    private final PaymentDao paymentDao = new PaymentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        try {
            request.setAttribute("announcements", announcementDao.findLatest());
            request.setAttribute("complaints", complaintDao.findForResident(user.getId()));
            request.setAttribute("visitors", visitorDao.findForResident(user.getId()));
            request.setAttribute("payments", paymentDao.findForResident(user.getId()));
            request.getRequestDispatcher("/WEB-INF/views/resident-dashboard-v2.jsp").forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }
}
