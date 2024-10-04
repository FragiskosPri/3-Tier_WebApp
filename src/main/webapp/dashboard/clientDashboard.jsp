<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mainpackage.Client" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="mainpackage.Phonecall" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page session="true" %>

<%


    if (session == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    Client client = (Client) session.getAttribute("client");

    if (client == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    String username = client.getUsername();
    String message = "Welcome, " + username + "!";
%>


<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/utilities/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/utilities/main.css">

    <link rel="icon" href="https://cdn-icons-png.freepik.com/512/126/126523.png" type="image/x-icon">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

    <title>Ναι ναι μ'ακούτε;</title>
</head>

<body>

<!--Header menu button-->
<div class="header">
    <div class="headerButton">
        <label class="myLabel">
            <input type="checkbox" id="check"/>
            <span></span>
            <span></span>
            <span></span>
        </label>
    </div>
    <img alt="logo" src="${pageContext.request.contextPath}/utilities/logo.png" class="logo">
</div>

<!--Sidebar-->
<div class="sidebar" id="sidebar">
    <a onclick="changePanel('makeCall', 4)" id="makeCallButton">Make a call</a>
    <a onclick="changePanel('viewCallHistory',4)" id="viewHistoryButton">View call history</a>
    <a onclick="changePanel('viewBill', 4)" id="viewBillButton">View Bill</a>


    <a href="<%= request.getContextPath() %>/logout">Logout</a>
</div>


<div class="content" id="content">
    <div class="centerWrapper" style="margin-top: 2%;">
        <h1><%= message %>
        </h1>
    </div>

    <!--Make Call-->
    <div id="makeCall" style="display: none;">
        <div class="centerWrapper">
            <div class="login-box">
                <h2>Make a call</h2>
                <form id="editClientForm" action="${pageContext.request.contextPath}/makeCall" method="post">

                    <div class="user-box">
                        <input type="tel" name="phonenumber" required oninput="validateNumericInput(this)">
                        <label>Telephone Number</label>
                    </div>


                    <div class="user-box">
                        <input type="text" name="duration" required oninput="validateNumericInput(this)">
                        <label>Duration</label>
                    </div>

                    <div class="centerWrapper">
                        <button type="reset" onclick="backToTable()" class="button-50" style="margin-right:5%">Back
                        </button>
                        <button type="submit" class="button-50">Make Call</button>
                    </div>

                </form>
            </div>
        </div>
    </div>

    <!--Bill-->
    <div id="viewBill" style="display: none ;">
        <div class="centerWrapper">
            <div class="login-box">
                <h2>Make payment of: <%= client.getPayment() %> €</h2>
                <form id="payBill" method="post" action="${pageContext.request.contextPath}/payBill">

                    <div class="centerWrapper">
                        <button type="submit" class="button-50">Make Payment</button>
                    </div>

                </form>
            </div>
        </div>
    </div>

    <!--Call History-->
    <div class="table-wrapper" id="viewCallHistory" style="display: none;">
        <table class="fl-table" id="history-Data">
            <thead>
            <tr>
                <th onclick="sortTable(0,'history-Data')">Caller</th>
                <th onclick="sortTable(1,'history-Data')">Receiver</th>
                <th onclick="sortTable(2,'history-Data')">Duration <small> (mins) </small></th>
                <th onclick="sortTable(3,'history-Data')">Date</th>
            </tr>

            </thead>

            <tbody>
                <%
                    ArrayList<Phonecall> phonecalls = client.getPhonecalls();
                      if (phonecalls == null || phonecalls.isEmpty()) {
                %>
            <tr>
                <td colspan="6" style="text-align: center;">No phone calls found</td>
            </tr>
                <%
                    } else {
                        for (Phonecall phonecall : phonecalls) {
                %>

            <tr>
                <td><%= phonecall.getCaller() %>
                </td>
                <td><%= phonecall.getReceiver() %>
                </td>
                <td><%= phonecall.getDuration() %>
                </td>
                <td><%= phonecall.getDate() %>
                </td>
            </tr>
                <%
                        }
                    }
                %>
            <tbody>

        </table>
    </div>

</div>
</body>

<script type="text/javascript" src="${pageContext.request.contextPath}/utilities/visualScript.js"></script>

</html>