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

@WebServlet("/createProgram")
public class createProgram extends HttpServlet {
    private static final Logger logger = Logger.getLogger(createProgram.class.getName());
    @Resource(name = "jdbc/users")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message;
        Administrator admin = userUtils.verifyAdmin(request, response);
        if (admin == null) {
            message = "Couldn't find admin session!";
            userUtils.goToErrorPage(request, response, message);
            return;
        }

        String talkingTime = request.getParameter("talkingTime");
        String fixedCharge = request.getParameter("fixedCharge");
        String extraCharge = request.getParameter("extraCharge");

        String sellerID = request.getParameter("sellerID");
        if (!userUtils.sellerExists(dataSource, Integer.parseInt(sellerID))){
            message = "The sellerID given does not exist!";
            logger.warning(message);
            userUtils.goToErrorPage(request, response, message);
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        logger.info("Creating program...");
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO program (talkingTime, fixedCharge, extraCharge, sellerID) VALUES (?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, talkingTime);
            ps.setString(2, fixedCharge);
            ps.setString(3, extraCharge);
            ps.setString(4, sellerID);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                message = "Program created successfully!";
                logger.info(message);
            }


        } catch (Exception e) {
            message = "There was an error creating the program " + e.getMessage();
            logger.severe(message);
            userUtils.goToErrorPage(request, response, message);
            return;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        userUtils.refreshAdmin(request, dataSource, admin);
        request.getRequestDispatcher("index.jsp").forward(request, response);


    }

}
