package com.clientFunctions;

import mainpackage.Client;
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
import java.sql.SQLException;

@WebServlet("/payBill")
public class payBill extends HttpServlet {
    @Resource(name = "jdbc/users")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message;
        Client client = userUtils.verifyClient(request, response);

        if (client == null) {
            message = "Couldn't find client session!";
            userUtils.goToErrorPage(request, response, message);
            return;
        }

        if (client.getPayment() == 0) {
            userUtils.refreshClient(request, client);
            if (!response.isCommitted()) {
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        boolean success = false;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            String sql = "UPDATE phonenumber SET bill = 0 WHERE AFM = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, client.getAFM());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                success = true;
            }

        } catch (Exception e) {
            message = "Error, couldn't pay the bill: " + e.getMessage();
            userUtils.goToErrorPage(request, response, message);
            return;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                message = "Error, couldn't close connection: " + e.getMessage();
                userUtils.goToErrorPage(request, response, message);
                return;
            }
        }

        if (success) {
            client.setPayment(0);
            userUtils.refreshClient(request, client);
            if (!response.isCommitted()) {
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        } else {
            message = "Error paying the bill!";
            userUtils.goToErrorPage(request, response, message);
        }
    }



}
