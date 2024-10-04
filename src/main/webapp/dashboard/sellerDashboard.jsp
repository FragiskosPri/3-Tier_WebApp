<%@ page import="mainpackage.Client" %>
<%@ page import="mainpackage.Program" %>
<%@ page import="mainpackage.Seller" %>
<%@ page import="java.util.ArrayList" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page session="true" %>
<%
    // Check if a session exists and retrieve the seller object from the session
    if (session == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    Seller seller = (Seller) session.getAttribute("seller");

    if (seller == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return; // Stop further processing of the page
    }

    String username = seller.getUsername();
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

    <style>
        .error {
            color: red;
        }
    </style>

</head>

<body>

<!--Header menu button-->
<div class="header">
    <div class="headerButton">
        <label class="myLabel">
            <input type="checkbox" id="check" />
            <span></span>
            <span></span>
            <span></span>
        </label>
    </div>
    <img alt="logo" src="${pageContext.request.contextPath}/utilities/logo.png" class="logo">
</div>

<!--Sidebar-->
<div class="sidebar" id="sidebar">
    <a onclick="changePanel('programTable',5)" id="viewPrograms">View Programs</a>
    <a onclick="changePanel('clientForm',  5)" id="registerClient">Register Client</a>
    <a onclick="changePanel('clientTable', 5)" id="editClient">Edit Client</a>
    <a onclick="changePanel('billClient', 5)" id="billClientButton">Bill Client</a>
    <a href="<%= request.getContextPath() %>/logout">Logout</a>
</div>


<div class="content" id="content">
    <!--Welcome message-->
    <div class="centerWrapper" id="welcomeMessage">
        <h1>
            <%= message %>
        </h1>
    </div>

    <!--View programs table -->
    <div class="table-wrapper" id="programTable" style="display: none;">
        <table class="fl-table" id="programTable-Data">
            <thead>
            <tr>
                <th onclick="sortTable(0,'programTable-Data')">program ID</th>
                <th onclick="sortTable(1,'programTable-Data')">Avail. Talking Time</th>
                <th onclick="sortTable(2,'programTable-Data')">Fixed Charge</th>
                <th onclick="sortTable(3,'programTable-Data')">Extra Charge</th>
            </tr>

            </thead>

            <tbody>
            <%
                // Retrieve the list of programs associated with the seller
                ArrayList<Program> programs = seller.getProgramList();
                if (programs == null || programs.isEmpty()) {
            %>
            <tr>
                <td colspan="4" style="text-align: center;">No program found</td>
            </tr>
            <%
            } else {
                for (Program program : programs) {
            %>
            <tr>
                <td><%= program.getProgramID() %>
                </td>
                <td><%= program.getTalkingTime() %>
                </td>
                <td><%= program.getFixedCharge() %>
                </td>
                <td><%= program.getExtraCharge() %>
                </td>
            </tr>
            <%
                    }
                }
            %>
            </tbody>


        </table>

    </div>

    <!--Register new Client -->
    <div id="clientForm" style="display: none;">
        <div class="centerWrapper">
            <div class="login-box">
                <h2>Register Client</h2>
                <form id="registerClientForm" action="${pageContext.request.contextPath}/createClient"
                      method="post">
                    <div class="user-box">
                        <input type="text" name="name" required>
                        <label>First Name</label>
                    </div>

                    <div class="user-box">
                        <input type="text" name="surname" required>
                        <label>Last Name</label>
                    </div>

                    <div class="user-box">
                        <input type="text" name="username" required>
                        <label>Username</label>
                    </div>

                    <div class="centerWrapper">
                        <div class="user-box" style="margin-right: 2%">
                            <input type="password" id="password" name="password" required>
                            <label>Password</label>
                        </div>

                        <div class="user-box" style="margin-left: 2% ">
                            <input type="password" id="confirmPassword" name="password2" required>
                            <label>Confirm Password</label>
                        </div>
                    </div>

                    <div class="user-box">
                        <input type="tel" name="phonenumber" oninput="validateNumericInput(this)" required>
                        <label>Telephone Number</label>
                    </div>

                    <div class="user-box">
                        <input type="text" name="progID" required oninput="validateNumericInput(this)">
                        <label>Program ID</label>
                    </div>

                    <div style="text-align: center;">
                        <button type="submit" class="button-50">Submit</button>
                    </div>
                    <div class="centerWrapper">
                        <p id="error-message" class="error"></p>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!--Edit client-->
    <div id="clientTable" style="display: none;">

        <!--Searchbar and Table-->
        <div id="searchAndTable">
            <div class="searchWrapper">
                <input class="searchInput" type="text" placeholder="Search Client..." id="clientSearch"
                       name="clientSearch">
                <button class="searchButton" onclick="searchTable('clientTable-Data','clientSearch')">
                    <i class="fa fa-search"></i>
                </button>
            </div>


            <table id="clientTable-Data" class="fl-table">
                <thead>
                <tr>
                    <th onclick="sortTable(0,'clientTable-Data')">AFM</th>
                    <th onclick="sortTable(1,'clientTable-Data')">First Name</th>
                    <th onclick="sortTable(2,'clientTable-Data')">Surname</th>
                    <th onclick="sortTable(3,'clientTable-Data')">Username</th>
                    <th onclick="sortTable(4,'clientTable-Data')">Phone number</th>
                    <th onclick="sortTable(5,'clientTable-Data')">Program ID</th>
                </tr>
                </thead>

                <tbody>
                <% ArrayList<Client> clients = seller.getClientList();
                    if (clients == null || clients.isEmpty()) {
                %>
                <tr>
                    <td colspan="6" style="text-align: center;">No clients found</td>
                </tr>
                <% } else { for (Client client : clients) { %>
                <tr onclick="populateClientForm(this)">
                    <td>
                        <%= client.getAFM() %>
                    </td>
                    <td>
                        <%= client.getName() %>
                    </td>
                    <td>
                        <%= client.getSurname() %>
                    </td>
                    <td>
                        <%= client.getUsername() %>
                    </td>
                    <td>
                        <%= client.getPhoneNumber() %>
                    </td>
                    <td>
                        <%= client.getProgramID() %>
                    </td>
                </tr>
                <% } } %>
                </tbody>


            </table>
        </div>

        <!--Edit Form-->
        <div class="centerWrapper">
            <div class="login-box" id="editClientDiv" style="display: none;">
                <h2>Edit Client</h2>
                <form id="editClientForm" action="${pageContext.request.contextPath}/editClient" method="post">
                    <div class="user-box">
                        <input type="text" name="name" required>
                        <label>First Name</label>
                    </div>
                    <div class="user-box">
                        <input type="text" name="surname" required>
                        <label>Last Name</label>
                    </div>
                    <div class="user-box">
                        <input type="text" name="username" required>
                        <label>Username</label>
                    </div>
                    <div class="user-box">
                        <input type="tel" name="phonenumber" required oninput="validateNumericInput(this)">
                        <label>Telephone Number</label>
                    </div>

                    <div class="centerWrapper">
                        <div class="user-box">
                            <input type="text" name="progID" required oninput="validateNumericInput(this)">
                            <label>Program ID</label>
                        </div>

                        <div class="user-box" style="margin-left: 3%">
                            <input type="text" name="AFM" readonly>
                            <label style="top: -20px; left: 0; font-size: 12px">AFM:</label>


                        </div>
                    </div>

                    <div class="centerWrapper">
                        <button type="reset" onclick="backToTable('searchAndTable', 'editClientDiv')"
                                class="button-50" style="margin-right:5%">Back
                        </button>
                        <button type="submit" class="button-50">Submit</button>
                    </div>

                </form>
            </div>
        </div>


        <script>
            // Call the setupDateValidation function from the external JavaScript file
            document.addEventListener('DOMContentLoaded', function () {
                setupDateValidation('billFrom', 'billUntil');
            });
        </script>

    </div>


    <div id="billClient" style="display: none;">

        <!--Bill client Search and Table-->
        <div id="billClient-Search-Table">
            <!--Search button-->
            <div class="searchWrapper">
                <input class="searchInput" type="text" placeholder="Search Client..." id="clientBillSearch"
                       name="clientSearch">
                <button class="searchButton" onclick="searchTable('clientBillTable-Data','clientBillSearch')">
                    <i class="fa fa-search"></i>
                </button>
            </div>

            <!--Client table-->
            <table id="clientBillTable-Data" class="fl-table">
                <thead>
                <tr>
                    <th onclick="sortTable(0,'clientBillTable-Data')">AFM</th>
                    <th onclick="sortTable(1,'clientBillTable-Data')">First Name</th>
                    <th onclick="sortTable(2,'clientBillTable-Data')">Surname</th>
                    <th onclick="sortTable(3,'clientBillTable-Data')">Username</th>
                    <th onclick="sortTable(4,'clientBillTable-Data')">Phone number</th>
                    <th onclick="sortTable(5,'clientBillTable-Data')">Bill</th>
                    <th onclick="sortTable(6,'clientBillTable-Data')">Program ID</th>
                </tr>
                </thead>

                <tbody>
                <%
                    if (clients == null || clients.isEmpty()) {
                %>
                <tr>
                    <td colspan="6" style="text-align: center;">No clients found</td>
                </tr>
                <% } else { for (Client client : clients) { %>
                <tr onclick="populateBillForm(this)">
                    <td><%= client.getAFM() %></td>
                    <td><%= client.getName() %></td>
                    <td><%= client.getSurname() %></td>
                    <td><%= client.getUsername() %></td>
                    <td><%= client.getPhoneNumber() %></td>
                    <td><%= client.getPayment() %></td>
                    <td><%= client.getProgramID() %></td>
                </tr>
                <% } } %>
                </tbody>


            </table>

        </div>

        <!--Billing form-->
        <div id="billClientForm" style="display: none">
            <div class="centerWrapper">
                <div class="login-box">
                    <h2>Bill Client</h2>
                    <form action="${pageContext.request.contextPath}/billClient" method="post">

                        <!--Username and AFM-->
                        <div class="centerWrapper">
                            <div class="user-box" style="margin-left: 3%; width: 30%;">
                                <input type="text" name="AFM" readonly>
                                <label style="top: -20px; left: 0; font-size: 12px">AFM:</label>
                            </div>

                            <div class="user-box" style="margin-left: 3%">
                                <input type="text" name="username" readonly>
                                <label style="top: -20px; left: 0; font-size: 12px">Username:</label>
                            </div>
                        </div>


                        <!--Date picker-->
                        <div class="centerWrapper">
                            <label for="billFrom" style="margin-right: 2%;"> From:</label>
                            <input type="date" id="billFrom" name="billFrom" required>

                            <label for="billFrom" style="margin-left: 2%;"> Until:</label>
                            <input type="date" id="billUntil" name="billUntil" required>
                        </div>

                        <div class="centerWrapper">
                            <button type="reset" onclick="backToTable('billClient-Search-Table', 'billClientForm')"
                                    class="button-50" style="margin-right:5%">Back</button>
                            </button>

                            <div style="text-align: center;">
                                <button type="submit" class="button-50">Submit</button>
                            </div>
                        </div>
                    </form>

                </div>
            </div>

        </div>

    </div>


</div>
</body>

<script>
    document.getElementById('registerClientForm').addEventListener('submit', function (event) {
        var password = document.getElementById('password').value;
        var confirmPassword = document.getElementById('confirmPassword').value;
        var errorMessage = document.getElementById('error-message');

        if (password !== confirmPassword) {
            errorMessage.textContent = 'Passwords do not match!';
            event.preventDefault();
        } else {
            errorMessage.textContent = '';
        }
    });
</script>

<script type="text/javascript" src="${pageContext.request.contextPath}/utilities/visualScript.js"></script>


</html>