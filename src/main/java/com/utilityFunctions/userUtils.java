package com.utilityFunctions;

import mainpackage.Administrator;
import mainpackage.Client;
import mainpackage.Program;
import mainpackage.Seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class userUtils {

    //--- Client
    private static final Logger logger = Logger.getLogger(userUtils.class.getName());

    public static Client verifyClient(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false); // Ensure session is not created if it doesn't exist

        if (session == null || session.getAttribute("client") == null) {
            logger.warning("Client session is missing or expired.");
            try {
                response.sendRedirect("index.jsp");
            } catch (IOException e) {
                logger.warning("Error while trying to redirect to index.jsp. " + e.getMessage());
            }
            return null;
        }

        return (Client) session.getAttribute("client");
    }

    public static void refreshClient(HttpServletRequest request, Client client) {
        HttpSession session = request.getSession(false); // Ensure session is not created if it doesn't exist
        session.setAttribute("client", client);
    }

    //--- Seller
    public static Seller verifySeller(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false); // Ensure session is not created if it doesn't exist

        if (session == null || session.getAttribute("seller") == null) {
            String message = "Seller session is missing or expired.";
            logger.warning(message);
            try {
                response.sendRedirect("index.jsp");
            } catch (IOException e) {

                logger.warning("Error redirecting to index.jsp. Error: " + e.getMessage());
            }
            return null;
        }

        return (Seller) session.getAttribute("seller");
    }

    public static void refreshSeller(HttpServletRequest request, DataSource dataSource, Seller seller) {
        HttpSession session = request.getSession(false);

        int sellerID = seller.getSellerID();
        seller.setClientList(userUtils.getClientsFromDb(sellerID, dataSource));
        seller.setProgramList(userUtils.getProgramsFromDb(sellerID, dataSource));

        session.setAttribute("seller", seller);
        logger.info("Refreshed seller session");
    }

    public static ArrayList<Client> getClientsFromDb(int sellerID, DataSource dataSource) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        PreparedStatement ps2 = null;
        ResultSet rs2 = null;

        Connection conn = null;

        ArrayList<Client> clientList = new ArrayList<>();
        try {
            conn = dataSource.getConnection();

            //--- Add the corresponding client to the seller
            // Fetch clients associated with the sellerID based on programID
            String sql = "SELECT c.*, p.programID FROM client c " + "JOIN phonenumber p ON c.phoneNumber = p.phoneNumber " + "JOIN program pr ON p.programID = pr.programID " + "WHERE pr.sellerID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sellerID);
            rs = ps.executeQuery();

            while (rs.next()) {
                Client client = new Client();
                client.setUsername(rs.getString("username"));
                client.setName(rs.getString("name"));
                client.setSurname(rs.getString("surname"));
                client.setPhoneNumber(rs.getString("phoneNumber"));
                client.setAFM(rs.getInt("AFM"));
                client.setProgramID(rs.getInt("programID"));

                // Get the amount owed
                sql = "SELECT * FROM phonenumber WHERE AFM = ?";
                ps2 = conn.prepareStatement(sql);
                ps2.setInt(1, client.getAFM());
                rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    // You should use rs2.getFloat("bill") instead of rs.getFloat("bill")
                    client.setPayment(rs2.getFloat("bill"));
                }


                clientList.add(client);
            }
        } catch (Exception e) {
            logger.warning("Error getting clients from database " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (rs2 != null) rs2.close();
                if (ps2 != null) ps2.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }

        return clientList;
    }

    public static ArrayList<Program> getProgramsFromDb(int sellerID, DataSource dataSource) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Program> programList = new ArrayList<>();

        try {
            conn = dataSource.getConnection();
            String sql = "SELECT * FROM program WHERE sellerID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sellerID);
            rs = ps.executeQuery();

            while (rs.next()) {
                Program program = new Program();
                program.setProgramID(rs.getInt("programID"));
                program.setTalkingTime(rs.getInt("talkingTime"));
                program.setFixedCharge(rs.getFloat("fixedCharge"));
                program.setExtraCharge(rs.getFloat("extraCharge"));
                programList.add(program);
            }

        } catch (Exception e) {
            logger.warning("Error getting programs from database " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return programList;
    }

    //--- Admin
    public static Administrator verifyAdmin(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false); // Ensure session is not created if it doesn't exist

        if (session == null || session.getAttribute("admin") == null) {
            logger.warning("Admin session is missing or expired.");
            try {
                response.sendRedirect("index.jsp");
            } catch (IOException e) {
                logger.warning("Error redirecting to index.jsp. Error: " + e.getMessage());
            }
            return null;
        }
        logger.info("Verified admin session.");
        return (Administrator) session.getAttribute("admin");
    }

    public static void refreshAdmin(HttpServletRequest request, DataSource dataSource, Administrator admin) {
        HttpSession session = request.getSession();
        admin.setSellerList(getSellersFromDb(dataSource));
        admin.setProgramList(getProgramsFromDb(dataSource));

        session.setAttribute("admin", admin);
        logger.info("Refreshed admin session.");
    }

    public static ArrayList<Seller> getSellersFromDb(DataSource dataSource) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;

        ArrayList<Seller> sellerList = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            String sql = "SELECT * FROM seller";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Seller seller = new Seller();
                seller.setSellerID(rs.getInt("sellerID"));
                seller.setName(rs.getString("name"));
                seller.setSurname(rs.getString("surname"));
                seller.setUsername(rs.getString("username"));
                sellerList.add(seller);
            }
        } catch (Exception e) {
            logger.warning("There was an error loading sellers" + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        logger.info("Added the sellers from the database");
        return sellerList;
    }

    public static ArrayList<Program> getProgramsFromDb(DataSource dataSource) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Program> programList = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            String sql = "SELECT * FROM program";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Program program = new Program();
                program.setProgramID(rs.getInt("programID"));
                program.setTalkingTime(rs.getInt("talkingTime"));
                program.setFixedCharge(rs.getFloat("fixedCharge"));
                program.setExtraCharge(rs.getFloat("extraCharge"));
                program.setSellerID(rs.getInt("sellerID"));
                programList.add(program);
            }

        } catch (Exception e) {
            logger.warning("There was an error loading programs" + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        logger.info("Added the programs from the database");
        return programList;
    }

    //--- Other
    public static void goToErrorPage(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, ServletException {
        if (!response.isCommitted()) {
            HttpSession session = request.getSession(false); // Do not create a new session if one does not exist
            if (session == null) {
                // Handle session expiration
                message = "Session has expired. " + message;
            }
            logger.warning(message);

            // Write error message with CSS styling directly to the response
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.write("<!DOCTYPE html>");
                out.write("<html>");
                out.write("<head>");
                out.write("<meta charset=\"UTF-8\">");
                out.write("<title>Error</title>");
                out.write("<link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/main.css\">");
                out.write("</head>");
                out.write("<body>");
                out.write("<h1 class=\"header\">Status:</h1>");
                out.write("<div class=\"centerWrapper\">");
                out.write("<div class=\"login-box\" style=\"text-align: center;\">");
                out.write("<p>" + message + "</p>");
                out.write("<p>Please try again.</p>");
                out.write("<br>");
                out.write("<a href=\"" + request.getContextPath() + "/index.jsp\">Back</a>");
                out.write("</div>");
                out.write("</div>");
                out.write("</body>");
                out.write("</html>");
            } catch (IOException e) {
                logger.severe("Failed to write error message to response: " + e.getMessage());
            }
        } else {
            // Response already committed, so we can't modify it
            logger.warning("Response was already committed. Cannot modify or forward.");
            // Optionally write an additional message to the log or do nothing if the response is already sent
        }
    }

    public static boolean uniqueUsername(DataSource dataSource, String username) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = dataSource.getConnection();

            // Combined SQL query to check username in all tables
            String sql = "SELECT * FROM (" + "SELECT username FROM client WHERE username = ? " + "UNION " + "SELECT username FROM seller WHERE username = ? " + "UNION " + "SELECT username FROM administrator WHERE username = ?" + ") AS combined";

            ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, username);
            ps.setString(3, username);

            rs = ps.executeQuery();

            return !rs.next();

        } catch (Exception e) {
            logger.warning("There was an error in checking the validity of the username" + e.getMessage());
            return false;
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

    public static boolean sellerExists(DataSource datasource, int sellerID) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        logger.info("Checking existence of seller with the sellerID: " + sellerID);
        try {
            conn = datasource.getConnection();
            String sql = "SELECT * FROM seller WHERE sellerID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sellerID);
            rs = ps.executeQuery();

            return rs.next();
        } catch (Exception e) {
            logger.warning("Error verifying the existence of seller with the seller ID:" + sellerID + "\n" + e.getMessage());
            return false;
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
