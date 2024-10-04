package com.clientFunctions;

import com.utilityFunctions.userUtils;
import mainpackage.Client;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mainpackage.Phonecall;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/makeCall")
public class makeCall extends HttpServlet {
    @Resource(name = "jdbc/users")
    private DataSource dataSource;

    private static final Logger LOGGER = Logger.getLogger(makeCall.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Starting doPost method");

        String message;
        Client client = userUtils.verifyClient(request, response);

        if (client == null) {
            message = "Couldn't find client session!";
            LOGGER.severe(message);
            userUtils.goToErrorPage(request, response, message);
            return;
        }
        LOGGER.info("Client verified: " + client);

        // Values from form
        String receiverNumber = request.getParameter("phonenumber");
        int duration;
        try {
            duration = Integer.parseInt(request.getParameter("duration"));
        } catch (NumberFormatException e) {
            message = "Invalid duration format!";
            LOGGER.severe(message);
            userUtils.goToErrorPage(request, response, message);
            return;
        }
        LOGGER.info("Received parameters - receiverNumber: " + receiverNumber + ", duration: " + duration);

        boolean success = false;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            LOGGER.info("Database connection established");

            String sqlSelect = "SELECT * FROM phonenumber WHERE phonenumber = ?";
            psSelect = conn.prepareStatement(sqlSelect);
            psSelect.setString(1, receiverNumber);
            rs = psSelect.executeQuery();
            LOGGER.info("Executed query: " + sqlSelect);

            if (rs.next()) {
                int receiverAFM = rs.getInt("AFM");
                LOGGER.info("Receiver AFM found: " + receiverAFM);

                String sqlInsert = "INSERT INTO callhistory (callerAFM, callerNumber, receiverAFM, receiverNumber, duration, date) VALUES (?, ?, ?, ?, ?, ?)";
                psInsert = conn.prepareStatement(sqlInsert);
                psInsert.setInt(1, client.getAFM());
                psInsert.setString(2, client.getPhoneNumber());
                psInsert.setInt(3, receiverAFM);
                psInsert.setString(4, receiverNumber);
                psInsert.setInt(5, duration);
                psInsert.setDate(6, java.sql.Date.valueOf(java.time.LocalDate.now()));

                psInsert.executeUpdate();
                LOGGER.info("Inserted call history record");

                Phonecall phonecall = new Phonecall();
                phonecall.setCaller(client.getPhoneNumber());
                phonecall.setReceiver(receiverNumber);
                phonecall.setDuration(duration);
                phonecall.setDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
                ArrayList<Phonecall> phonecalls = client.getPhonecalls();
                phonecalls.add(phonecall);
                client.setPhonecalls(phonecalls);

                conn.commit();
                LOGGER.info("Transaction committed");
                success = true;
            } else {
                message = "Receiver number not found!";
                LOGGER.warning(message);
                userUtils.goToErrorPage(request, response, message);
                return;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error making call", e);
            message = "Error making call: " + e.getMessage();
            userUtils.goToErrorPage(request, response, message);
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.info("Transaction rolled back");
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Error during rollback", rollbackEx);
                }
            }
            return;

        } finally {
            try {
                if (rs != null) rs.close();
                if (psSelect != null) psSelect.close();
                if (psInsert != null) psInsert.close();
                if (conn != null) conn.close();
                LOGGER.info("Resources closed");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources", e);
            }
        }

        if (success) {
            userUtils.refreshClient(request, client);
            request.getRequestDispatcher("index.jsp").forward(request, response);
            LOGGER.info("Call successful, forwarding to index.jsp");
        } else {
            message = "Couldn't make call!";
            LOGGER.warning(message);
            userUtils.goToErrorPage(request, response, message);
        }
    }
}
