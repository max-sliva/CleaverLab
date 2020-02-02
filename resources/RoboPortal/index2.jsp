<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <!--link href="http://s3.amazonaws.com/codecademy-content/courses/ltp/css/shift.css" rel="stylesheet"-->
    <title>ГИС-сайт!</title>
    <link rel="stylesheet"  href="bootstrap.css">
    <link rel="stylesheet" href="main.css">
    
  </head>
                <% 
                	String userName = null;
				%>
  <body>
    <div class="navbar" style="margin: 0px;">
      <div class="container">
        <ul class="pull-left">
          <li><a href="#">О проекте</a></li>
                <% 
	            	if(session.getAttribute("user") != null){
	            		userName = (String) session.getAttribute("user");
	            		if (userName.equals("Admin")){
                %>
          <li><a href="admin.jsp">Admin Page!</a></li>
                <% 		}
	            	} 
                %>
          
        </ul>
        <ul class="pull-right">
          <li><a href="#">Cтруктура сайта</a></li>
                <% 
	            	if(session.getAttribute("user") == null){
                %>
          <li><a href="login.jsp">Авторизация!</a></li>
                <% }
	            	else { 
                %>
          <li><a href="LogoutServlet">Logout!</a></li>
                <% } 
                %>
          
          <li><a href="#">Помощь</a></li>
        </ul>
      </div>
    </div>

    <div class="jumbotron vertical-align">
      <div class="container">
        <h1>ИЗУЧАЕМ ГИС</h1>
        <p>Учебные материалы по изучению Геоинформационных систем <br>
        в Нижневартовском государственном университете</p>
        <!-- a href="#">Learn More</a-->
      </div>
    </div> 

    <div class="neighborhood-guides">
        <div class="container">
            <h2> Программное обеспечение ГИС</h2>
            <p> Задания лабораторных работ описаны для трех популярных настольных ГИС</p>
            <div class="row">
                <div class="col-md-4">
                    <h4><img src="Images/QG.png" > QGIS</h4>                
                    <div class="thumbnail">
                        <img src="Images/QGIS.png" >
                    </div>
                </div>
                <div class="col-md-4">
                    <h4> <img src="Images/MI.png" > MapInfo</h4>
                    <div class="thumbnail">
                        <img src="Images/MapInfo.png" >
                    </div>
                </div>
                <div class="col-md-4">
                    <h4> <img src="Images/AG.png" > ArcGIS</h4>
                    <div class="thumbnail">
                        <img src="Images/ArcGIS.png" >
                    </div>
                </div>
            </div>
            <div class="buttom">
            	<img src="Images/SlivaEA.png">
            	<h2> Занятия ведет</h2>
            	<p> Старший преподаватель <br>
            		кафедры Информатики и методики преподавания информатики <br>
            		Нижневартовского государственного университета <br>
            		<b>Слива Екатерина Александровна </b>
            	</p>
            	
            </div> 
        </div>
    </div>
 
    
<!--     <div class="learn-more">
	  <div class="container">
		<div class="row">
	      <div class="col-md-4">
			<h3>Презентации к лекциям</h3>
			<p>Презентационный материал для подготовки к занятиям, зачетам и экзаменам .</p>
			<p><a href="#">Посмотреть список презентаций</a></p>
	      </div>
		  <div class="col-md-4">
			<h3>Лабораторные работы</h3>
			<p>Лабораторные работы, составленные для разного ГИС ПО.</p>
			<p><a href="#">Посмотреть список лабораторных работ</a></p>
		  </div>
		  <div class="col-md-4">
			<h3>Проверочные материалы</h3>
			<p>Вопросы к экзаменам и зачетам, примерные тестовые задания для разных направлений обучения.</p>
			<p><a href="#">Посмотреть проверочные материалы</a></p>
		  </div>
	    </div>
	  </div>
	</div>
 -->  
 </body>
</html>