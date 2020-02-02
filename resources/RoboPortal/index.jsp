<%@ page language="java" contentType="text/html;  charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>RobotLab</title>
	<link rel="stylesheet"  href="bootstrap.css">
    <link rel="stylesheet" href="main.css">
	<script src="jquery.min.js"></script>
    <script src="bootstrap.min.js"></script>
<!-- 	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script> -->
<!--     <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script> -->
		<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
	</head>
                <% 
                	String userName = null;
				%>

	<body>
<!-- 	функция для определения, что капс нажат -->
	<script type="text/javascript">
		document.onkeypress = function ( e ) {
		    e = (e) ? e : window.event;
		
		    var kc = ( e.keyCode ) ? e.keyCode : e.which; // get keycode
		    var isUp = (kc >= 65 && kc <= 90) ? true : false; // uppercase
		    var isLow = (kc >= 97 && kc <= 122) ? true : false; // lowercase
		    var isShift = ( e.shiftKey ) ? e.shiftKey : ( (kc == 16) ? true : false ); // shift is pressed -- works for IE8-
		
		    // uppercase w/out shift or lowercase with shift == caps lock
		    if ( (isUp && !isShift) || (isLow && isShift) ) {
		        alert("CAPSLOCK is on."); // do your thing here
		    } else {
		        // no CAPSLOCK to speak of
		    }
	
		}
	</script>
    <div class="navbar" style="margin: 0px;">
      <div class="container">
        <ul class="pull-left">
          <li><a href="about.jsp">О портале</a></li>
                <% 
	            	if(session.getAttribute("user") != null){
	            		userName = (String) session.getAttribute("user");
	            		if (userName.equals("Admin")){
                %>
          <li><a href="admin.jsp">Admin Page!</a></li>
                <% 		
                		}
	            		else {
	                        %>
	      <li><a href="projects.jsp">Проекты</a></li>
	                        <% 		
	            		}
	            	} 
                %>
          
        </ul>
        <ul class="pull-right">
          <li><a href="#">Cтруктура сайта</a></li>
                <% 
	            	if(session.getAttribute("user") == null){
                %>
          <li>
<!--           <button type="button" class="btn btn-info btn-sm" data-toggle="modal" data-target="#myModal">Авторизация!</button> -->
          <a href="#myModal" data-target="#myModal" data-toggle="modal">Авторизация!</a></li>
                <% }
	            	else { 
                %>
          <li><a href="LogoutServlet">Logout!</a></li>
                <% } 
                %>
          
          <li><a href="help.jsp">Помощь</a></li>
        </ul>
      </div>
    </div>

	<div id="myModal" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		    <!-- Modal content-->
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Авторизация</h4>
		      </div>
		      <form action="Login" method="post" accept-charset="UTF-8">
			      <div class="modal-body">
			        <p>Введите данные</p>
			        <label>Логин:</label> <input id="login" name="login" type="text" size="15" maxlength="15" required>
			        <br />
					<label>пароль:</label> <input name="password" type="password" size="15" maxlength="15" required><br />
			      </div>
			      <div class="modal-footer">
			        <input type="submit" class="btn btn-default" value="Войти" > <!-- data-dismiss="modal" onclick="javascript: submit()"> -->
			        <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
			        <button type="button" class="btn btn-default" data-dismiss="modal">Отмена2/button>
			      </div>
		      </form>
		    </div>
		
		  </div>
	</div>


    <div class="jumbotron vertical-align">
     
<div class="container">
     
<!--         <h1>ИЗУЧАЕМ Робототехнику!</h1> -->
<!--         <p>Портал Студенческой лаборатории робототехники НВГУ<br> </p> -->
<!-- <a href="/weather.html" style="background: blue; width: 70px; height: 85px; border: 0; top: 10px; left: 8px; resize: none;"></a>       -->
      </div>
    </div> 
	</body>
</html>