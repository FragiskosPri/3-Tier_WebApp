<%@ page import="mainpackage.UserType" %>
<%@ page import="mainpackage.Administrator" %>
<%@ page import="mainpackage.Program" %>
<%@ page import="mainpackage.Seller" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
    if (session == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    Administrator admin = (Administrator) session.getAttribute("admin");

    if (admin == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    String username = admin.getUsername();
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
    <a onclick="changePanel('sellerForm',5)">Register Seller</a>
    <a onclick="changePanel('sellerEdit',5)">Edit Seller</a>


    <a onclick="changePanel('createProgram',5)">Register Program</a>
    <a onclick="changePanel('programEdit',5)">Edit Program</a>

    <a href="<%= request.getContextPath() %>/updatePasswords">Update Passwords</a>
    <a href="<%= request.getContextPath() %>/generateCalls">Generate Calls</a>
    <a href="<%= request.getContextPath() %>/logout">Logout</a>
</div>


<div class="content" id="content">

    <!--Welcome message-->
    <div class="centerWrapper" id="welcomeMessage">
        <h1>
            <%= message %>
        </h1>
    </div>

    <!--Create Seller -->
    <div id="sellerForm" style="display: none;">

        <div class="centerWrapper">
            <div class="login-box">


                <h2>Register Seller</h2>

                <form id="registerSellerForm" action="${pageContext.request.contextPath}/createSeller"
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


                    <div style="text-align: center;">
                        <button type="submit" class="button-50">Submit</button>
                    </div>

                </form>
            </div>
        </div>
    </div>

    <!--Edit Seller-->
    <div id="sellerEdit" style="display: none;">

        <!--Searchbar and Table-->
        <div id="editSeller-Search-Table">
            <!--Searchbar-->
            <div class="searchWrapper">
                <input class="searchInput" type="text" placeholder="Search Seller..." id="sellerSearch"
                       name="sellerSearch">
                <button class="searchButton" onclick="searchTable('sellerTable-Data','sellerSearch')">
                    <i class="fa fa-search"></i>
                </button>
            </div>

            <!--Table-->
            <table id="sellerTable-Data" class="fl-table">
                <thead>
                <tr>
                    <th onclick="sortTable(0,'sellerTable-Data')">sellerID</th>
                    <th onclick="sortTable(1,'sellerTable-Data')">First Name</th>
                    <th onclick="sortTable(2,'sellerTable-Data')">Surname</th>
                    <th onclick="sortTable(3,'sellerTable-Data')">Username</th>
                </tr>
                </thead>

                <tbody>
                <%
                    if (admin.getSellerList() == null || admin.getSellerList().isEmpty()) {
                %>
                <tr>
                    <td colspan="6" style="text-align: center;">No sellers found</td>
                </tr>
                <%
                } else {
                    for (Seller seller : admin.getSellerList()) {

                %>
                <tr onclick="populateSellerForm(this)">
                    <td>
                        <%= seller.getSellerID() %>
                    </td>
                    <td>
                        <%= seller.getName() %>
                    </td>
                    <td>
                        <%= seller.getSurname() %>
                    </td>
                    <td>
                        <%= seller.getUsername() %>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
                </tbody>


            </table>
        </div>

        <!--Edit Form-->

        <div class="centerWrapper">
            <div id="editSellerDiv" style="display: none ;">
                <div class="login-box">
                    <h2>Edit Seller</h2>
                    <form id="editSellerForm" action="${pageContext.request.contextPath}/editSeller" method="post">
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
                            <input type="text" name="SELLERID" readonly>
                            <label style="top: -20px; left: 0; font-size: 12px">Seller ID:</label>
                        </div>

                        <div class="centerWrapper">
                            <button type="reset" onclick="backToTable('editSeller-Search-Table', 'editSellerDiv')"
                                    class="button-50" style="margin-right:5%">Back
                            </button>
                            <button type="submit" class="button-50">Submit</button>
                        </div>

                    </form>
                </div>
            </div>
        </div>
    </div>

    <!--Create Program-->
    <div id="createProgram" style="display: none;">

        <div class="centerWrapper">
            <div class="login-box">

                <div class="centerWrapper">
                    <p id="error-message" class="error"></p>
                </div>

                <h2>Register Program</h2>

                <form id="registerProgramForm" action="${pageContext.request.contextPath}/createProgram"
                      method="post">

                    <div class="user-box">
                        <input type="text" name="talkingTime" oninput="validateNumericInput(this)" required>
                        <label>Avail. Talking Time</label>
                    </div>


                    <div class="user-box">
                        <input type="text" name="fixedCharge" id="fixedCharge"
                               oninput="handleNumberInputFocus(this)" required>
                        <label>Fixed Charge</label>
                    </div>

                    <div class="user-box">
                        <input type="text" name="extraCharge" id="extraCharge"
                               oninput="handleNumberInputFocus(this)" required>
                        <label>Extra Charge</label>
                    </div>

                    <div class="user-box">
                        <input type="text" name="sellerID" required>
                        <label>Program ID</label>
                    </div>

                    <div style="text-align: center;">
                        <button type="submit" class="button-50">Submit</button>
                    </div>

                </form>
            </div>
        </div>

    </div>

    <!--Edit Program-->
    <div id="programEdit" style="display: none;">

        <!--Searchbar and Table-->
        <div id="editProgram-Search-Table">
            <div class="searchWrapper">
                <input class="searchInput" type="text" placeholder="Search Program..." id="programSearch"
                       name="programSearch">
                <button class="searchButton" onclick="searchTable('programTable-Data','programSearch')">
                    <i class="fa fa-search"></i>
                </button>
            </div>


            <div class="table-wrapper" id="programTable">
                <table class="fl-table" id="programTable-Data">
                    <thead>
                    <tr>
                        <th onclick="sortTable(0,'programTable-Data')">program ID</th>
                        <th onclick="sortTable(1,'programTable-Data')">Avail. Talking Time</th>
                        <th onclick="sortTable(2,'programTable-Data')">Fixed Charge</th>
                        <th onclick="sortTable(3,'programTable-Data')">Extra Charge</th>
                        <th onclick="sortTable(3,'programTable-Data')">Seller ID</th>
                    </tr>

                    </thead>

                    <tbody>
                    <%
                        if (admin.getProgramList() == null || admin.getProgramList().isEmpty()) {
                    %>
                    <tr>
                        <td colspan="4" style="text-align: center;">No program found</td>
                    </tr>
                    <%
                    } else {
                        for (Program program : admin.getProgramList()) {

                    %>
                    <tr onclick="populateProgramForm(this)">
                        <td>
                            <%= program.getProgramID() %>
                        </td>
                        <td>
                            <%= program.getTalkingTime() %>
                        </td>
                        <td>
                            <%= program.getFixedCharge() %>
                        </td>
                        <td>
                            <%= program.getExtraCharge() %>
                        </td>
                        <td>
                            <%= program.getSellerID() %>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>


        <div class="centerWrapper">
            <div id="editProgramDiv" style="display: none;">
                <div class="login-box">

                    <h2>Edit Program</h2>

                    <form id="editProgramForm" action="${pageContext.request.contextPath}/editProgram"
                          method="post">

                        <div class="user-box">
                            <input type="text" name="talkingTime" oninput="validateNumericInput(this)" required>
                            <label>Avail. Talking Time</label>
                        </div>


                        <div class="user-box">
                            <input type="text" name="fixedCharge" id="fixedCharge"
                                   oninput="handleNumberInputFocus(this)" required>
                            <label>Fixed Charge</label>
                        </div>

                        <div class="user-box">
                            <input type="text" name="extraCharge" id="extraCharge"
                                   oninput="handleNumberInputFocus(this)" required>
                            <label>Extra Charge</label>
                        </div>

                        <div class="user-box">
                            <input type="text" name="sellerID" required>
                            <label>Seller ID</label>
                        </div>

                        <div class="user-box">
                            <input type="text" name="programID" readonly required>
                            <label style="top: -20px; left: 0; font-size: 12px">Seller ID</label>
                        </div>


                        <div class="centerWrapper">
                            <div style="text-align: center;">
                                <button type="reset" onclick="backToTable('editProgram-Search-Table', 'editProgramDiv' )"
                                        class="button-50">Back
                                </button>
                            </div>
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
    document.getElementById('registerSellerForm').addEventListener('submit', function (event) {
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