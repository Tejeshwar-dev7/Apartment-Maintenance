package com.apartment.servlet;

import com.apartment.dao.VisitorDao;
import com.apartment.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/resident/visitors")
public class VisitorServlet extends HttpServlet {
    private final VisitorDao visitorDao = new VisitorDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        try {
            visitorDao.create(
                    user.getId(),
                    request.getParameter("visitorName"),
                    request.getParameter("phone"),
                    request.getParameter("purpose"),
                    LocalDateTime.parse(request.getParameter("visitTime"))
            );
            response.sendRedirect(request.getContextPath() + "/resident/dashboard");
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }
}
