<%@ page language="java" contentType="text/html;  charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<!-- 	<link rel="stylesheet"  href="bootstrap.css"> -->
<!--     <link rel="stylesheet" href="main.css"> -->
	<script src="jquery.min.js"></script>
    <script src="bootstrap.min.js"></script>
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
	<%
    String choice=null;
	String userName = null;
	if(session.getAttribute("user") == null){
	    response.sendRedirect("index.jsp");
	} else {
		userName = (String) session.getAttribute("user");
		if (!userName.equals("Admin")) response.sendRedirect("index.jsp");
		String sessionID = null;
		Cookie[] cookies = request.getCookies();
		if(cookies !=null){
			for(Cookie cookie : cookies){
			    if(cookie.getName().equals("user")) userName = cookie.getValue();
			}
		}
	}
	%>


<title>Аккаунты</title>
</head>
<body>
	Аккаунты
	
</body>
</html>