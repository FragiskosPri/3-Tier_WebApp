package com.adminFunctions;

import com.utilityFunctions.userUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mainpackage.Administrator;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

@WebServlet("/editSeller")
public class editSeller extends HttpServlet {
    private static final Logger logger = Logger.getLogger(editSeller.class.getName());
    @Resource(name = "jdbc/users")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message;
        boolean success = false;
        Administrator admin = userUtils.verifyAdmin(request, response);
        if (admin == null) {
            message = "Couldn't find admin session!";
            userUtils.goToErrorPage(request, response, message);
            return;
        }

        String sellerId = request.getParameter("SELLERID");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String username = request.getParameter("username");

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            String sql = "UPDATE seller SET name = ?, surname = ?, username = ? WHERE sellerID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, surname);
            ps.setString(3, username);
            ps.setInt(4, Integer.parseInt(sellerId));
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
                conn.commit();
            }

        } catch (Exception e) {
            message = "Error while editing client!" + e.getMessage();
            logger.warning(message);
            userUtils.goToErrorPage(request, response, message);
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (success) {
            message = "Successfully edited seller!";
            logger.info(message);
            userUtils.refreshAdmin(request, dataSource, admin);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
