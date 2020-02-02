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
	String prName=request.getParameter("prName");
	session.setAttribute("projectName", prName);
// 	if(session.getAttribute("user") == null){
// 	    response.sendRedirect("index.jsp");
// 	} else {
// 		userName = (String) session.getAttribute("user");
// 		if (userName.isEmpty()) response.sendRedirect("index.jsp");
// 		String sessionID = null;
// 		Cookie[] cookies = request.getCookies();
// 		if(cookies !=null){
// 			for(Cookie cookie : cookies){
// 			    if(cookie.getName().equals("user")) userName = cookie.getValue();
// 			}
// 		}
// 	}
	%>


<title>Мой Проект</title>
</head>
<body>
	Мой Проект
	<jsp:include page="ShowProject" />
</body>
</html>