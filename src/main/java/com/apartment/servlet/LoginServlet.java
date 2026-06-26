package com.apartment.servlet;

import com.apartment.dao.UserDao;
import com.apartment.model.Role;
import com.apartment.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            User user = userDao.authenticate(request.getParameter("email"), request.getParameter("password"));
            if (user == null) {
                request.setAttribute("error", "Invalid email or password.");
                doGet(request, response);
                return;
            }

            request.getSession().setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + dashboardPath(user.getRole()));
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }

    private String dashboardPath(Role role) {
        return switch (role) {
            case ADMIN -> "/admin/dashboard";
            case STAFF -> "/staff/tasks";
            case RESIDENT -> "/resident/dashboard";
        };
    }
}
