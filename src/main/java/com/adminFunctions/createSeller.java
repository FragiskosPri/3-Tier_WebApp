package com.adminFunctions;

import mainpackage.Administrator;
import mainpackage.Password;

import com.utilityFunctions.userUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.logging.Logger;

@WebServlet("/createSeller")
public class createSeller extends HttpServlet {

    private static final Logger logger = Logger.getLogger(createSeller.class.getName());
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

        // Values from the form
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String username = request.getParameter("username");

        if (!userUtils.uniqueUsername(dataSource, username)) {
            message = "Username already exists!";
            userUtils.goToErrorPage(request, response, message);
            return;
        }

        char[] password = request.getParameter("password").toCharArray();
        byte[] salt = Password.generateSalt();
        byte[] hashedPassword = Password.hashPassword(password, salt);

        Arrays.fill(password, ' ');  // Clear password array

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            logger.info("Creating seller " + name);
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO seller (name, surname, username, salt, hash) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, surname);
            ps.setString(3, username);
            ps.setBytes(4, salt);
            ps.setBytes(5, hashedPassword);


            // Minimising the chance of exposure in memory
            Arrays.fill(salt, (byte) 0);  // Clear salt array
            Arrays.fill(hashedPassword, (byte) 0);  // Clear hash array

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                message = "Successfully created seller " + name;
                logger.info(message);
            }

        } catch (Exception e) {
            message = "There was an error while creating seller" + e.getMessage();
            userUtils.goToErrorPage(request, response, message);
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        userUtils.refreshAdmin(request, dataSource, admin);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
