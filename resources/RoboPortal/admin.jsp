<%@ page language="java" contentType="text/html;  charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>RobotLab AdminPage</title>
	<link rel="stylesheet"  href="bootstrap.css">
    <link rel="stylesheet" href="main.css">
	<script src="jquery.min.js"></script>
    <script src="bootstrap.min.js"></script>
<!-- 	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script> -->
<!--     <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script> -->
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
</head>
<body>
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
    <div class="navbar">
      <div class="container">
        <ul class="pull-left">
          <li><a href="index.jsp">Главная</a></li>
        </ul>
        <ul class="pull-right">
          <li><a href="#">Структура сайта</a></li>
          <li><a href="LogoutServlet">Logout!</a></li>
          <li><a href="help.jsp">Помощь</a></li>
        </ul>
      </div>
    </div>
    		<form action="AdminMainpageServlet" method="post" >
	            <div class="row">
                <div class="col-xs-6" align="center" >
					<% 
				    if(session.getAttribute("choice")==null) choice="showAccounts";
				    else    		
				    	choice=(String) session.getAttribute("choice");
				    if (choice.equals("showAccounts")){
				    %>
                        <input id="option1" type="radio" name="choice" checked onclick="javascript: submit()" value="showAccounts">
				    <% }  else { 
					    %>
                        <input id="option1" type="radio" name="choice" onclick="javascript: submit()" value="showAccounts">
				    	<% }
					    %>
                        <label for="option1"><span><span></span></span>Учетные записи</label>
                </div>
                <div class="col-xs-6" align="center" >
                    	<% if (choice.equals("showProjects")){ 
                    	%>
                        <input id="option2" type="radio" name="choice" checked onclick="javascript: submit()" value="showProjects">
				    	<% }  else { 
					    %>
                        <input id="option2" type="radio" name="choice" onclick="javascript: submit()" value="showProjects">
				    	<% }
					    %>
						<label for="option2"><span><span></span></span>Проекты 	</label>
                </div>
            </div>
		</form>
		<% 
	    if(session.getAttribute("choice")==null) {  
	    %>
	    	<jsp:include page="showAccounts.jsp" />	
	    <% }
	    else {   		
	        	choice=(String) session.getAttribute("choice");
	            if (choice.equals("showAccounts")){
	        %>
	          		<jsp:include page="showAccounts.jsp" />	
	        <% }
		        else if(choice.equals("showProjects")){
	        %>
	          		<jsp:include page="showProjects.jsp" />	
	        <%}
	    }        
        %>
    
</body>
</html>