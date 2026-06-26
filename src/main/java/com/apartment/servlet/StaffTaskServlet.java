package com.apartment.servlet;

import com.apartment.dao.AnnouncementDao;
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

@WebServlet("/staff/tasks")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 8 * 1024 * 1024)
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
            String completionPhotoUrl = saveCompletionPhoto(request);
            if (completionPhotoUrl == null) {
                completionPhotoUrl = request.getParameter("completionPhotoUrl");
            }
            complaintDao.updateProgress(
                    Integer.parseInt(request.getParameter("complaintId")),
                    user.getId(),
                    request.getParameter("status"),
                    completionPhotoUrl
            );
            response.sendRedirect(request.getContextPath() + "/staff/tasks");
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }
    }

    private String saveCompletionPhoto(HttpServletRequest request) throws IOException, ServletException {
        try {
            Part photo = request.getPart("completionPhoto");
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
            String uploadPath = getServletContext().getRealPath("/uploads/completed");
            File uploadDirectory = new File(uploadPath);
            if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
                throw new IOException("Unable to create upload directory");
            }

            File targetFile = new File(uploadDirectory, fileName);
            try (java.io.InputStream input = photo.getInputStream()) {
                java.nio.file.Files.copy(input, targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            // Also save to source directory for persistence
            String sourceUploadPath = "C:\\Users\\tejes\\OneDrive\\Documents\\Apartment Maintenance Management System\\src\\main\\webapp\\uploads\\completed";
            File sourceUploadDir = new File(sourceUploadPath);
            if (sourceUploadDir.exists() || sourceUploadDir.mkdirs()) {
                File sourceTarget = new File(sourceUploadDir, fileName);
                try (java.io.InputStream input2 = photo.getInputStream()) {
                    java.nio.file.Files.copy(input2, sourceTarget.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ignored) {}
            }

            return request.getContextPath() + "/uploads/completed/" + fileName;
        } catch (Exception e) {
            return null;
        }
    }
}
