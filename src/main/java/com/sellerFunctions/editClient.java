package com.sellerFunctions;

// My packages

import com.utilityFunctions.userUtils;
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

@WebServlet("/editClient")
public class editClient extends HttpServlet {

    @Resource(name = "jdbc/users")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message;
        Seller seller = userUtils.verifySeller(request, response);

        if (seller == null) {
            message = "Couldn't find seller session!";
            userUtils.goToErrorPage(request, response, message);
            return;
        }

        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String username = request.getParameter("username");
        String phonenumber = request.getParameter("phonenumber");
        String programID = request.getParameter("progID");
        String AFM = request.getParameter("AFM");

        boolean success = false;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            // Update client table
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            String clientSql = "UPDATE client SET name = ?, surname = ?, username = ?, phonenumber = ? WHERE AFM = ?";
            ps = conn.prepareStatement(clientSql);
            ps.setString(1, name);
            ps.setString(2, surname);
            ps.setString(3, username);
            ps.setString(4, phonenumber);
            ps.setString(5, AFM);
            ps.executeUpdate();

            // Update phonenumber table
            String phoneSql = "UPDATE phonenumber SET phonenumber = ? , programID = ? WHERE AFM = ?";
            ps = conn.prepareStatement(phoneSql);
            ps.setString(1, phonenumber);
            ps.setString(2, programID);
            ps.setString(3, AFM);

            int clientRowsAffected = ps.executeUpdate();
            int phonenumberRowsAffected = ps.executeUpdate();

            // Commit the transaction if both updates are successful
            if (clientRowsAffected > 0 && phonenumberRowsAffected > 0) {
                conn.commit();
                success = true;
                message = "Client and phone number updated successfully.";
            } else {
                message = "Something went wrong while updating client.";
                conn.rollback();
                message += "\nFailed to update client or phone number.";
            }

        } catch (SQLException e) {
            message = "Error updating client or phone number: " + e.getMessage();
            try {
                if (conn != null) conn.rollback();
                if (ps != null) ps.close();
            } catch (SQLException se) {
                message = "Error closing connections: " + se.getMessage();
            }
        }
        if (success) {
            userUtils.refreshSeller(request, dataSource, seller);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else userUtils.goToErrorPage(request, response, message);

    }
}