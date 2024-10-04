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

@WebServlet("/editProgram")
public class editProgram extends HttpServlet {
    private static final Logger logger = Logger.getLogger(editProgram.class.getName());
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

        String talkingTime = request.getParameter("talkingTime");
        String fixedCharge = request.getParameter("fixedCharge");
        String extraCharge = request.getParameter("extraCharge");
        String programID = request.getParameter("programID");

        String sellerID = request.getParameter("sellerID");
        if (!userUtils.sellerExists(dataSource, Integer.parseInt(sellerID))) {
            message = "The sellerID given does not exist!";
            logger.warning(message);
            userUtils.goToErrorPage(request, response, message);
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        logger.info("Editing program...");
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            String sql = "UPDATE program SET talkingTime = ?, fixedCharge = ?, extraCharge = ?, sellerID = ? WHERE programID = ? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, talkingTime);
            ps.setString(2, fixedCharge);
            ps.setString(3, extraCharge);
            ps.setString(4, sellerID);
            ps.setInt(5, Integer.parseInt(programID));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
                conn.commit();
            }

        } catch (Exception e) {
            message = "Error while editing program!" + e.getMessage();
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
            message = "Successfully edited program!";
            logger.info(message);
            userUtils.refreshAdmin(request, dataSource, admin);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
