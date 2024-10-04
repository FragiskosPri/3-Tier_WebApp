package com.loginServlet;

// My packages

import com.utilityFunctions.userUtils;
import mainpackage.*;

// Java imports
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.sql.DataSource;

// Servlet imports
import jakarta.annotation.Resource;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;


@WebServlet("/login")
public class login extends HttpServlet {
    private static final Logger logger = Logger.getLogger(login.class.getName());

    @Resource(name = "jdbc/users")
    private DataSource dataSource;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        char[] password = request.getParameter("password").toCharArray();

        Seller seller = loginSeller(username, password);
        if (seller != null) {
            HttpSession session = request.getSession();
            session.setAttribute("seller", seller);
            session.setAttribute("userType", UserType.SELLER);
            request.getRequestDispatcher("dashboard/sellerDashboard.jsp").forward(request, response);
            return;

        }

        Client client = loginClient(username, password);
        if (client != null) {
            HttpSession session = request.getSession();
            session.setAttribute("client", client);
            session.setAttribute("userType", UserType.CLIENT);
            request.getRequestDispatcher("dashboard/clientDashboard.jsp").forward(request, response);
            return;
        }

        Administrator admin = loginAdmin(username, password);
        if (admin != null) {
            HttpSession session = request.getSession();
            session.setAttribute("admin", admin);
            session.setAttribute("userType", UserType.ADMINISTRATOR);
            request.getRequestDispatcher("dashboard/clientDashboard.jsp").forward(request, response);
            return;
        }

        String message = "Username or password incorrect!";
        userUtils.goToErrorPage(request, response, message);


    }

    private Seller loginSeller(final String username, final char[] password) {
        Seller seller = new Seller();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        logger.info("Trying to login seller");
        try {
            conn = dataSource.getConnection();
            String sql = "SELECT * FROM seller WHERE username = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);


            rs = ps.executeQuery();

            if (!rs.next()) {
                logger.warning("No seller found");
                return null;
            }

            // Replicate the hash with the salt and check if it matches for log-in
            byte[] salt = rs.getBytes("salt");
            byte[] hash = rs.getBytes("hash");

            byte[] hashedPass = Password.hashPassword(password, salt);

            if (!Arrays.equals(hashedPass, hash)) return null;

            // Minimising the chance of exposure in memory
            Arrays.fill(password, ' ');  // Clear password array
            Arrays.fill(salt, (byte) 0);  // Clear salt array
            Arrays.fill(hash, (byte) 0);  // Clear hash array

            seller.setSellerID(rs.getInt("sellerID")); // This is needed to assign the proper clients to the seller
            seller.setUsername(username);
            seller.setName(rs.getString("name"));
            seller.setSurname(rs.getString("surname"));
            rs.close();
            ps.close();

            int sellerID = seller.getSellerID();
            seller.setClientList(userUtils.getClientsFromDb(sellerID, dataSource));
            seller.setProgramList(userUtils.getProgramsFromDb(sellerID, dataSource));
            return seller;

        } catch (Exception e) {
            logger.warning("Error while trying to login seller: " + e.getMessage());
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warning("Error while trying to close connection: " + e.getMessage());
            }


        }
    }

    private Client loginClient(final String username, final char[] password) {
        Client client;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        logger.info("Trying to login client");
        try {
            conn = dataSource.getConnection();
            String sql = "SELECT * FROM client WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            rs = ps.executeQuery();

            if (!rs.next()) {
                logger.warning("No client found");
                return null;
            }

            // Replicate the hash with the salt and check if it matches for log-in
            byte[] salt = rs.getBytes("salt");
            byte[] hash = rs.getBytes("hash");
            byte[] hashedPass = Password.hashPassword(password, salt);

            if (!Arrays.equals(hashedPass, hash)) {
                logger.info("Password inserted incorrectly");
                return null;
            }

            // Minimising the chance of exposure in memory
            Arrays.fill(password, ' ');  // Clear password array
            Arrays.fill(salt, (byte) 0);  // Clear salt array
            Arrays.fill(hash, (byte) 0);  // Clear hash array


            client = new Client();
            client.setAFM(rs.getInt("AFM"));
            client.setName(rs.getString("name"));
            client.setSurname(rs.getString("surname"));
            client.setUsername(username);
            client.setPhoneNumber(rs.getString("phonenumber"));

            rs.close();
            ps.close();
            sql = "SELECT * FROM phonenumber WHERE AFM = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, client.getAFM());

            rs = ps.executeQuery();

            if (rs.next()) {
                float payment = rs.getFloat("bill");
                client.setPayment(payment);
            }

            rs.close();
            ps.close();
            sql = "SELECT * FROM callhistory WHERE callerAFM = ? OR receiverAFM = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, client.getAFM());
            ps.setInt(2, client.getAFM());

            rs = ps.executeQuery();

            logger.info("Getting phone calls from database");
            ArrayList<Phonecall> phoneCallList = new ArrayList<>();
            while (rs.next()) {
                Phonecall phonecall = new Phonecall();
                phonecall.setCaller(rs.getString("callerNumber"));
                phonecall.setReceiver(rs.getString("receiverNumber"));
                phonecall.setDuration(rs.getInt("duration"));
                phonecall.setDate(rs.getDate("date"));
                phoneCallList.add(phonecall);
            }
            client.setPhonecalls(phoneCallList);

            logger.info("Client found and created instance successfully");
            return client;


        } catch (SQLException e) {
            // Handle any SQL exceptions appropriately
            logger.warning("Error while trying to login client: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Administrator loginAdmin(final String username, final char[] password) {
        Administrator admin;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        logger.info("Trying to login admin");
        try {
            conn = dataSource.getConnection();
            String sql = "SELECT * FROM administrator WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (!rs.next()) {
                logger.warning("No admin found");
                return null;
            }
            // Replicate the hash with the salt and check if it matches for log-in
            byte[] salt = rs.getBytes("salt");
            byte[] hash = rs.getBytes("hash");
            byte[] hashedPass = Password.hashPassword(password, salt);

            if (!Arrays.equals(hashedPass, hash)) {
                logger.info("Password inserted incorrectly");
                return null;
            }

            Arrays.fill(password, ' ');
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(hash, (byte) 0);

            admin = new Administrator();
            admin.setName(rs.getString("name"));
            admin.setSurname(rs.getString("surname"));
            admin.setUsername(username);


            sql = "SELECT * FROM seller";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            ArrayList<Seller> sellerList = userUtils.getSellersFromDb(dataSource);
            admin.setSellerList(sellerList);

            ArrayList<Program> programList = userUtils.getProgramsFromDb(dataSource);
            admin.setProgramList(programList);

            logger.info("Admin found and created instance successfully");
            return admin;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
