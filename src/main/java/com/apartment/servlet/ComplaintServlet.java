package com.apartment.servlet;

import com.apartment.dao.ComplaintDao;
import com.apartment.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/resident/complaints")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 8 * 1024 * 1024)
public class ComplaintServlet extends HttpServlet {
    private final ComplaintDao complaintDao = new ComplaintDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        try {
            String photoUrl = saveComplaintPhoto(request);
            complaintDao.create(
                    user.getId(),
                    request.getParameter("title"),
                    request.getParameter("category"),
                    request.getParameter("description"),
                    photoUrl
            );
            response.sendRedirect(request.getContextPath() + "/resident/dashboard");
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }

    private String saveComplaintPhoto(HttpServletRequest request) throws IOException, ServletException {
        Part photo = request.getPart("photo");
        if (photo == null || photo.getSize() == 0) {
            return null;
        }

        String submittedName = new File(photo.getSubmittedFileName()).getName();
        String extension = "";
        int dotIndex = submittedName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = submittedName.substring(dotIndex);
        }

        String fileName = UUID.randomUUID() + extension;
        String uploadPath = getServletContext().getRealPath("/uploads/complaints");
        File uploadDirectory = new File(uploadPath);
        if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
            throw new IOException("Unable to create upload directory");
        }

        photo.write(new File(uploadDirectory, fileName).getAbsolutePath());
        return request.getContextPath() + "/uploads/complaints/" + fileName;
    }
}
