package com.apartment.servlet;

import com.apartment.dao.PaymentDao;
import com.apartment.model.Payment;
import com.apartment.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/resident/payments/pay", "/resident/payments/receipt"})
public class PaymentServlet extends HttpServlet {
    private final PaymentDao paymentDao = new PaymentDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        try {
            paymentDao.markPaid(Integer.parseInt(request.getParameter("paymentId")), user.getId());
            response.sendRedirect(request.getContextPath() + "/resident/dashboard");
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        try {
            Payment payment = paymentDao.findPaidReceipt(Integer.parseInt(request.getParameter("paymentId")), user.getId());
            if (payment == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=receipt-" + payment.getTransactionId() + ".txt");
            try (PrintWriter writer = response.getWriter()) {
                writer.println("Apartment Maintenance Receipt");
                writer.println("Flat: " + payment.getFlatNumber());
                writer.println("Month: " + payment.getMonth());
                writer.println("Amount: Rs. " + payment.getAmount());
                writer.println("Transaction: " + payment.getTransactionId());
                writer.println("Paid On: " + payment.getPaidOn());
            }
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }
}
