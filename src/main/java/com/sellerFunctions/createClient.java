package com.sellerFunctions;

// My packages

import com.adminFunctions.createSeller;
import com.utilityFunctions.userUtils;
import mainpackage.Client;
import mainpackage.Password;
import mainpackage.Seller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

@WebServlet("/createClient")
public class createClient extends HttpServlet {
    private static final Logger logger = Logger.getLogger(createClient.class.getName());

    @Resource(name = "jdbc/users")
    private DataSource dataSource;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message;

        Seller seller = userUtils.verifySeller(request, response);

        if (seller == null) {
            message = "Couldn't find seller session! ";
            userUtils.goToErrorPage(request, response, message);
            return;
        }

        // Values from the form
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String username = request.getParameter("username");
        String phonenumber = request.getParameter("phonenumber");
        String programID = request.getParameter("progID");

        char[] password = request.getParameter("password").toCharArray();
        byte[] salt = Password.generateSalt();
        byte[] hashedPassword = Password.hashPassword(password, salt);

        Arrays.fill(password, ' ');  // Clear password array

        Connection conn = null;

        logger.info("Creating Client...");
        try {
            if (!userUtils.uniqueUsername(dataSource, username)) {
                message = "Username already exists! ";
                logger.info("Couldn't create client: " + message);
                userUtils.goToErrorPage(request, response, message);
                return;
            }
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            try (PreparedStatement psClient = conn.prepareStatement("INSERT INTO client (name, surname, username, phonenumber, salt, hash) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                psClient.setString(1, name);
                psClient.setString(2, surname);
                psClient.setString(3, username);
                psClient.setString(4, phonenumber);
                psClient.setBytes(5, salt);
                psClient.setBytes(6, hashedPassword);


                int rowsInserted = psClient.executeUpdate();
                if (rowsInserted > 0) {
                    Arrays.fill(salt, (byte) 0);  // Clear salt array
                    Arrays.fill(hashedPassword, (byte) 0);  // Clear hash array

                    try (ResultSet generatedKeys = psClient.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int AFMInserted = generatedKeys.getInt(1);

                            Client client = new Client();
                            client.setAFM(AFMInserted);
                            client.setName(name);
                            client.setSurname(surname);
                            client.setUsername(username);
                            client.setPhoneNumber(phonenumber);
                            client.setProgramID(Integer.parseInt(programID));

                            ArrayList<Client> clientList = seller.getClientList();
                            clientList.add(client);
                            seller.setClientList(clientList);

                            try (PreparedStatement psPhoneNumber = conn.prepareStatement("INSERT INTO phonenumber (phonenumber, bill, programID, AFM) VALUES (?, ?, ?, ?)")) {
                                psPhoneNumber.setString(1, phonenumber);
                                psPhoneNumber.setInt(2, 0);
                                psPhoneNumber.setInt(3, Integer.parseInt(programID));
                                psPhoneNumber.setInt(4, AFMInserted);

                                psPhoneNumber.executeUpdate();
                                conn.commit(); // Commit transaction

                                message = "Client created successfully with AFM: " + AFMInserted;
                                logger.info(message);
                            }
                        } else {
                            message = "Failed to retrieve generated AFM.";
                            conn.rollback();
                            userUtils.goToErrorPage(request, response, message);
                            return;
                        }
                    }
                } else {
                    message = "Failed to create client.";
                    conn.rollback();
                    userUtils.goToErrorPage(request, response, message);
                    return;
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                message = "Failed to create client: Duplicate entry.";
                conn.rollback();
                userUtils.goToErrorPage(request, response, message);
                return;
            } catch (SQLException e) {
                message = "Failed to create client: " + e.getMessage();
                conn.rollback();
                userUtils.goToErrorPage(request, response, message);
                return;
            }
        } catch (Exception e) {
            message = "Failed to create client: " + e.getMessage();
            userUtils.goToErrorPage(request, response, message);
            return;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        userUtils.refreshSeller(request, dataSource ,seller);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
