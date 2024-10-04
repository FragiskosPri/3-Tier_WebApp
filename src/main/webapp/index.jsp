<%@ page import="mainpackage.UserType" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session != null) {
        UserType userType = (UserType) session.getAttribute("userType");

        if (userType != null) {
            switch (userType) {
                case CLIENT:
                    response.sendRedirect("dashboard/clientDashboard.jsp");
                    break;
                case SELLER:
                    response.sendRedirect("dashboard/sellerDashboard.jsp");
                    break;
                case ADMINISTRATOR:
                    response.sendRedirect("dashboard/adminDashboard.jsp");
                    break;
                default:
                    // Handle unexpected userType (optional)
                    response.sendRedirect("error.jsp");
                    break;
            }
        }
    }
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="utilities/dashboard.css">
    <link rel="stylesheet" href="utilities/main.css">

    <link rel="icon" href="https://cdn-icons-png.freepik.com/512/126/126523.png" type="image/x-icon">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

    <title>Ναι ναι μ'ακούτε;</title>

    <style>


        h1 {
            text-align: center;
            color: #333;
        }

        .team {
            display: flex;
            justify-content: space-around;
            flex-wrap: wrap;
        }

        .team-member {
            width: 45%;
            margin: 10px 0;
            background-color: #f1f1f1;
            padding: 10px;
            border-radius: 5px;
        }

        .team-member h3 {
            margin-top: 0;
        }

        .team-member p {
            margin-bottom: 0;
        }

        .centered-hr {
            width: 50%;
            margin: 20px auto;
            border: 0;
            height: 1px;
            background-image: linear-gradient(to right, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.75), rgba(0, 0, 0, 0));
        }


        div.gallery {
            border: 1px solid #ccc;
        }

        div.gallery:hover {
            border: 1px solid #777;
        }

        div.gallery img {
            width: 100%;
            height: auto;
        }

        div.desc {
            padding: 15px;
            text-align: center;
        }

        * {
            box-sizing: border-box;
        }

        .responsive {
            padding: 0 6px;
            float: left;
            width: 24.99999%;
        }

        @media only screen and (max-width: 700px) {
            .responsive {
                width: 49.99999%;
                margin: 6px 0;
            }
        }

        @media only screen and (max-width: 500px) {
            .responsive {
                width: 100%;
            }
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
    <img alt="logo" src="utilities/logo.png" class="logo">
</div>

<!--Sidebar-->
<div class="sidebar" id="sidebar">
    <a onclick="changePanel('loginForm', 3)" id="loginButton">Login!</a>
    <a onclick="changePanel('aboutUs', 3)" id="aboutUsButton">About Us!</a>
    <a onclick="changePanel('ourShops', 3)" id="ourShopsButton">Our Shops</a>
</div>


<div class="content" id="content">
    <!--LOGIN-->
    <div id="loginForm">
        <div class="centerWrapper">

            <div class="login-box">
                <h2>Login</h2>
                <form action="login" method="post">

                    <div class="user-box">
                        <input type="text" id="username" name="username" required>
                        <label>Username</label>
                    </div>

                    <div class="user-box">
                        <input type="password" id="password" name="password" required>
                        <label>Password</label>
                    </div>

                    <div style="text-align: center;">
                        <button type="submit" class="button-50">Submit</button>
                    </div>

                </form>
            </div>
        </div>
    </div>

    <!--ABOUT US-->
    <div id="aboutUs" style="display: none;">
        <h1 style="margin: 2%;">About Us</h1>
        <p>Welcome to "Ναι ναι μ'ακούτε"! We are an innovative telecommunications company committed to providing you
            with the best communication experience possible. Since our founding in 2024, we have aimed to bring
            technology closer to you in ways you've never seen before.</p>
        <br>
        <p>At "Ναι ναι μ'ακούτε", we believe that communication is not just a tool, but a way of life. Whether it's
            managing your calls from the mountains or ensuring your canary can sing in HD quality, we are here for
            you.</p>

        <br>
        <p>Our philosophy is based on the four pillars of technology, innovation, human touch, and love for nature.
            Our services range from classic telephone lines to satellite communications for your exotic space
            travel.</p>

        <hr class="centered-hr">

        <h2 style="margin: 1% ;" class="centerWrapper">Our Team</h2>
        <div class="team">
            <div class="team-member">
                <h3>Fragiskos Printezis</h3>
                <p>Founder & CEO</p>
                <p>With over 20 years of experience in telecommunications, Fragiskos started "Ναι ναι μ'ακούτε" to
                    change
                    the way we see communication.</p>
            </div>

            <div class="team-member">
                <h3>George Korompilis</h3>
                <p>Graphic Designer</p>
                <p>George Korompilis is the creative force behind our visual identity. With a keen eye for detail
                    and a passion for innovative design</p>
            </div>

            <div class="team-member">
                <h3>George Zoumpoulidis</h3>
                <p>Director of Innovation</p>
                <p>George is responsible for developing new technologies that keep "Ναι ναι μ'ακούτε" at the
                    forefront of the communication revolution.</p>
            </div>

            <div class="team-member">
                <h3>Evangelos Strapatsas</h3>
                <p>Director of Human Resources</p>
                <p>Evangelos ensures that every team member has the support and resources they need to succeed.
                </p>
            </div>


        </div>

        <hr class="centered-hr">

        <div class="centerWrapper" style="margin-top: 2%;">
            <h2>Thank you for visiting our website. We hope to see you soon as members of the
                "Ναι ναι μ'ακούτε" family!
            </h2>
        </div>
    </div>

    <!--OUR SHOPS-->
    <div id="ourShops" style=" margin:1%; display: none;">
        <h2>Our Stores</h2>
        <p style="margin-bottom: 2%;">At "Ναι ναι μ'ακούτε", we are proud to serve you from multiple locations
            across Greece. Each of our stores is designed to provide you with a welcoming and efficient experience,
            offering a full range of our products and services. Here’s where you can find us:</p>


        <div class="responsive">
            <div class="gallery">
                <img src="stores/store1.png" alt="store1" width="600" height="400">
                <div class="desc">Our Galatsi store is located in the heart of the city, providing easy access to
                    all our latest products and services. Whether you need help with your current plan or are
                    looking to upgrade your device, our friendly staff is here to assist you.
                </div>
            </div>
        </div>

        <div class="responsive">
            <div class="gallery">
                <img src="stores/store2.png" alt="store2" width="600" height="400">
                <div class="desc">Located in the beautiful suburb of Pefki, this store offers a cozy and relaxed
                    atmosphere. Our team here is dedicated to helping you find the perfect solutions for your
                    communication needs, with personalized service and expert advice.
                </div>
            </div>
        </div>

        <div class="responsive">
            <div class="gallery">
                <img src="stores/store3.png" alt="store3" width="600" height="400">
                <div class="desc">Our Trikala store is a hub for technology enthusiasts in the region. With a wide
                    selection of the latest gadgets and accessories, we are your go-to destination for all things
                    telecommunications.
                </div>
            </div>
        </div>

        <div class="responsive">
            <div class="gallery">
                <img src="stores/store4.png" alt="store4" width="600" height="400">
                <div class="desc">Located on the beautiful island of Naxos, our store here combines convenience with
                    a stunning location. Whether you're a local resident or a visitor, we provide top-notch service
                    and the latest in communication technology.
                </div>
            </div>
        </div>
    </div>


</div>
</body>

<script type="text/javascript" src="utilities/visualScript.js"></script>

</html>