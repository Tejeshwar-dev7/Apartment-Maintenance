package com.apartment.servlet;

import com.apartment.dao.AnnouncementDao;
import com.apartment.dao.ComplaintDao;
import com.apartment.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/staff/tasks")
public class StaffTaskServlet extends HttpServlet {
    private final AnnouncementDao announcementDao = new AnnouncementDao();
    private final ComplaintDao complaintDao = new ComplaintDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        try {
            request.setAttribute("announcements", announcementDao.findLatest());
            request.setAttribute("tasks", complaintDao.findAssignedToStaff(user.getId()));
            request.getRequestDispatcher("/WEB-INF/views/staff-tasks-v2.jsp").forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        try {
            complaintDao.updateProgress(
                    Integer.parseInt(request.getParameter("complaintId")),
                    user.getId(),
                    request.getParameter("status"),
                    request.getParameter("completionPhotoUrl")
            );
            response.sendRedirect(request.getContextPath() + "/staff/tasks");
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }
}
