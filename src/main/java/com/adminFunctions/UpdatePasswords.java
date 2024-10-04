package com.adminFunctions;

import com.utilityFunctions.userUtils;
import jakarta.servlet.annotation.WebServlet;
import mainpackage.Administrator;
import mainpackage.Password;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/updatePasswords")
public class UpdatePasswords extends HttpServlet {

    @Resource(name = "jdbc/users")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Administrator admin = userUtils.verifyAdmin(request, response);
        if (admin == null) {
            String message = "Couldn't find admin session!";
            try {
                userUtils.goToErrorPage(request, response, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        updatePasswordInDatabase();
        response.getWriter().println("Passwords updated successfully!");
    }

    private void updatePasswordInDatabase() {
        // This is to generate passwords that were previously saved as strings

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // Disable auto-commit for transaction control

            // Update passwords for sellers
            String selectSellerSql = "SELECT * FROM seller";
            try (PreparedStatement selectSellerStmt = conn.prepareStatement(selectSellerSql);
                 ResultSet sellerRs = selectSellerStmt.executeQuery()) {

                while (sellerRs.next()) {
                    String password = sellerRs.getString("password");
                    if (password == null || password.isEmpty()) continue;
                    int id = sellerRs.getInt("sellerID");

                    byte[] salt = Password.generateSalt();
                    byte[] hashedPassword = Password.hashPassword(password.toCharArray(), salt);

                    String updateSellerSql = "UPDATE seller SET salt = ?, hash = ? WHERE sellerID = ?";
                    try (PreparedStatement updateSellerStmt = conn.prepareStatement(updateSellerSql)) {
                        updateSellerStmt.setBytes(1, salt);
                        updateSellerStmt.setBytes(2, hashedPassword);
                        updateSellerStmt.setInt(3, id);
                        updateSellerStmt.executeUpdate();
                    }
                }
            }

            // Update passwords for clients
            String selectClientSql = "SELECT * FROM client";
            try (PreparedStatement selectClientStmt = conn.prepareStatement(selectClientSql);
                 ResultSet clientRs = selectClientStmt.executeQuery()) {

                while (clientRs.next()) {
                    String password = clientRs.getString("password");
                    if (password == null || password.isEmpty()) continue;
                    int afm = clientRs.getInt("AFM");

                    byte[] salt = Password.generateSalt();
                    byte[] hashedPassword = Password.hashPassword(password.toCharArray(), salt);

                    String updateClientSql = "UPDATE client SET salt = ?, hash = ? WHERE AFM = ?";
                    try (PreparedStatement updateClientStmt = conn.prepareStatement(updateClientSql)) {
                        updateClientStmt.setBytes(1, salt);
                        updateClientStmt.setBytes(2, hashedPassword);
                        updateClientStmt.setInt(3, afm);
                        updateClientStmt.executeUpdate();
                    }
                }
            }

            String selectAdminSQL= "SELECT * FROM administrator";
            try (PreparedStatement selectAdmin = conn.prepareStatement(selectAdminSQL);
                 ResultSet adminRs = selectAdmin.executeQuery()) {
                while (adminRs.next()) {
                    String password = adminRs.getString("password");
                    if (password == null || password.isEmpty()) continue;
                    int ID = adminRs.getInt("ID");
                    byte[] salt = Password.generateSalt();
                    byte[] hashedPassword = Password.hashPassword(password.toCharArray(), salt);

                    String updateAdminSql = "UPDATE administrator SET salt = ?, hash = ? WHERE ID = ?";
                    try (PreparedStatement updateAdminStmt = conn.prepareStatement(updateAdminSql)) {
                        updateAdminStmt.setBytes(1, salt);
                        updateAdminStmt.setBytes(2, hashedPassword);
                        updateAdminStmt.setInt(3, ID);
                        updateAdminStmt.executeUpdate();
                    }
                }
            }


            conn.commit(); // Commit all updates at once

        } catch (SQLException e) {
            try (Connection conn = dataSource.getConnection()) {
                conn.rollback(); // Rollback changes if an error occurs
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }


}
