package com.sellerFunctions;

import mainpackage.Program;
import mainpackage.Seller;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebServlet("/billClient")
public class billClient extends HttpServlet {
    private static final Logger logger = Logger.getLogger(billClient.class.getName());

    @Resource(name = "jdbc/users")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String message;
        Seller seller = userUtils.verifySeller(request, response);
        if (seller == null) {
            message = "Couldn't find seller session!";
            logger.warning(message);
            userUtils.goToErrorPage(request, response, message);
            return;
        }

        String AFM = request.getParameter("AFM");
        String fromDate = request.getParameter("billFrom");
        String untilDate = request.getParameter("billUntil");

        boolean success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // Add all the calls between the time given to bill
            String sql = "SELECT * FROM callhistory WHERE callerAFM = ? AND date BETWEEN ? AND ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, AFM);
            ps.setString(2, fromDate);
            ps.setString(3, untilDate);
            rs = ps.executeQuery();

            int totalTime = 0;
            while (rs.next()) totalTime += (rs.getInt("duration"));

            logger.info("Total call duration for AFM " + AFM + " from " + fromDate + " to " + untilDate + " is " + totalTime);

            sql = "SELECT * FROM phonenumber p " +
                    "JOIN program pr ON p.programID = pr.programID " +
                    "WHERE p.AFM = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, AFM);
            rs = ps.executeQuery();

            Program program = new Program();
            while (rs.next()) {
                program.setTalkingTime(rs.getInt("talkingTime"));
                program.setExtraCharge(rs.getFloat("extraCharge"));
                program.setFixedCharge(rs.getFloat("fixedCharge"));
            }

            // Calculate the cost and update the client's billing information
            float cost = program.calculateCost(totalTime);
            logger.info("Calculated cost for AFM " + AFM + " is " + cost);

            sql = "UPDATE phonenumber SET bill = ? WHERE AFM = ? ";
            ps = conn.prepareStatement(sql);
            ps.setFloat(1, cost);
            ps.setString(2, AFM);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                success = true;
                message = "Successfully completed the bill!";
                logger.info(message);
            } else {
                message = "Something went wrong while billing the client!";
                logger.warning(message);
                conn.rollback();
                userUtils.goToErrorPage(request, response, message);
                return;
            }

        } catch (Exception ex) {
            message = "Error in billing client: " + ex.getMessage();
            logger.log(Level.SEVERE, message, ex);
            userUtils.goToErrorPage(request, response, message);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error closing resources", ex);
            }
        }

        if (success) {
            userUtils.refreshSeller(request, dataSource, seller);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else {
            userUtils.goToErrorPage(request, response, message);
        }
    }

}
