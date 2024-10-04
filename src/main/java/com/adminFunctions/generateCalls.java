package com.adminFunctions;

import com.utilityFunctions.ReceiverInfo;
import com.utilityFunctions.userUtils;
import jakarta.servlet.http.HttpServlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.sql.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Calendar;

// Java imports
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.DataSource;

// Servlet imports
import jakarta.annotation.Resource;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import mainpackage.Administrator;

@WebServlet("/generateCalls")
public class generateCalls extends HttpServlet {

    @Resource(name = "jdbc/users")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Administrator admin = userUtils.verifyAdmin(request, response);
        if (admin == null) {
            String message = "Couldn't find admin session!";
            userUtils.goToErrorPage(request, response, message);
            return;
        }
        generateCallsInDatabase();

        userUtils.refreshAdmin(request, dataSource, admin);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    private void generateCallsInDatabase() {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement insertPs = null;
        ResultSet rs = null;


        try {
            conn = dataSource.getConnection();
            String sql = "SELECT * FROM phonenumber";
            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();


            Date date;
            int duration;
            ReceiverInfo receiver;

            while (rs.next()) {
                String caller = rs.getString("phonenumber");
                int callerAFM = rs.getInt("AFM");
                int timesGenerated = getRandomNumOfCalls();
                for (int i = 0; i < timesGenerated; i++) {
                    date = getRandomDate();
                    duration = getRandomDuration();

                    receiver = getRandomReceiver(conn, caller);

                    String insertSql = "INSERT INTO callhistory (callerAFM, callerNumber, receiverAFM, receiverNumber, duration, date) VALUES (?,?,?,?,?,?)";
                    insertPs = conn.prepareStatement(insertSql);
                    insertPs.setInt(1, callerAFM);
                    insertPs.setString(2, caller);
                    insertPs.setInt(3, receiver.getAFM());
                    insertPs.setString(4, receiver.getPhonenumber());
                    insertPs.setInt(5, duration);
                    insertPs.setDate(6, date);
                    insertPs.executeUpdate();
                }
            }

        } catch (
                Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (insertPs != null) insertPs.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    private ReceiverInfo getRandomReceiver(Connection conn, String caller) throws SQLException {
        String receiver = caller;
        int AFM = -1;

        while (receiver.equals(caller)) {
            String calleeSql = "SELECT phonenumber, AFM FROM phonenumber ORDER BY RAND() LIMIT 1";
            try (PreparedStatement calleePs = conn.prepareStatement(calleeSql); ResultSet calleeRs = calleePs.executeQuery()) {
                if (calleeRs.next()) {
                    receiver = calleeRs.getString("phonenumber");
                    AFM = calleeRs.getInt("AFM");
                }
            }
        }

        return new ReceiverInfo(receiver, AFM);
    }


    private static final Random random = new Random();

    // Generate a random recent date
    public static Date getRandomDate() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -2);
        long twoMonthsAgo = calendar.getTimeInMillis();
        long randomTime = ThreadLocalRandom.current().nextLong(twoMonthsAgo, now);
        return new Date(randomTime);
    }

    public static int getRandomDuration() {
        return random.nextInt(120) + 1; // Up to 2 hours (120 minutes)
    }

    public static int getRandomNumOfCalls() {
        return random.nextInt(40);
    }


}

