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

        File targetFile = new File(uploadDirectory, fileName);
        try (java.io.InputStream input = photo.getInputStream()) {
            java.nio.file.Files.copy(input, targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        // Also save to source directory if possible, so that it persists during redeployments
        String sourceUploadPath = "C:\\Users\\tejes\\OneDrive\\Documents\\Apartment Maintenance Management System\\src\\main\\webapp\\uploads\\complaints";
        File sourceUploadDir = new File(sourceUploadPath);
        if (sourceUploadDir.exists() || sourceUploadDir.mkdirs()) {
            File sourceTarget = new File(sourceUploadDir, fileName);
            try (java.io.InputStream input2 = photo.getInputStream()) {
                java.nio.file.Files.copy(input2, sourceTarget.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ignored) {}
        }

        return request.getContextPath() + "/uploads/complaints/" + fileName;
    }
}
