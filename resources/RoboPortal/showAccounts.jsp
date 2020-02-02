<%@ page language="java" contentType="text/html;  charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
	<script type="text/javascript">
		var currentId=0; //groupCreate

	</script>
<title>Аккаунты</title>
</head>
<body>
	Аккаунты
	<jsp:include page="AccountsShowServlet" />
		<table	border="1" cellpadding="5" align="center">
            <caption>List of users!</caption>
            <tr>
                <th>Login</th>
                <th>FIO</th>
                <th>Group</th>
                <th>email</th>
            </tr>
		<c:forEach items="${accountsInfo}" var="account">
	        <tr>
	        	<td style="display: none;"><input type="hidden" name="id" value=<c:out value="${account.id}" />> </td>
	            <td><c:out value="${account.login}" /></td>
	            <td><c:out value="${account.FIO}" /></td>
	            <td><c:out value="${account.group}" /></td>
	            <td><c:out value="${account.email}" /></td>
	            <td>			
	            		<button type="button" class="btn btn-info btn-sm" onclick="getCurRow(this)">Изменить</button>
<!-- 	            	<button type="button" class="btn btn-info btn-sm" data-toggle="modal" data-target="#myModalEdit">Изменить</button> -->
				</td>
				<td>
	            		<button type="button" class="btn btn-info btn-sm" onclick="getCurRowForDelete(this)">Удалить</button>
				</td>
	        </tr>
    	</c:forEach>
    	<tr>
			<td>
			</td>
			<td>
				<button type="button" class="btn btn-info btn-sm" data-toggle="modal" data-target="#myModal">Добавить пользователя</button>
			</td>
			<td>
			</td>
			<td>
			</td>
        </tr>    
	</table>
	<script type="text/javascript">
		function getCurRow(x) {
// 		    alert("Row index is: " + x.rowIndex);
			var currentRow = x.closest('tr');
			currentId = currentRow.cells[0].getElementsByTagName('input')[0].value;
		    var idField=document.getElementById("userId");
		    idField.value=currentId;

		    var currentFIO=currentRow.cells[2].innerHTML;
		    var fioField=document.getElementById("FIO");
		    fioField.value=currentFIO;

		    var currentGroup=currentRow.cells[3].innerHTML;
		    var groupField=document.getElementById("groupEdit");
		    groupField.value=currentGroup;

		    var currentEmail=currentRow.cells[4].innerHTML;
		    var emailField=document.getElementById("emailEdit");
		    emailField.value=currentEmail;

// 		    alert(currentFIO); //emailEdit
		    
			var span = document.getElementById("closeEdit");
		    span.onclick = function() {
		        modal.style.display = "none";
		    }
		    var closeButton= document.getElementById("closeEditButton");
		    closeButton.onclick = function() {
		        modal.style.display = "none";
		    }
		    window.onclick = function(event) {
		        if (event.target == modal) {
		            modal.style.display = "none";
		        }
		    }			
// 		    alert(currentRow.rowIndex+" "+currentId);
		    var modal = document.getElementById('myModalEdit');
		    modal.style.display = "block";
		}
	</script>

	<script type="text/javascript">
		function getCurRowForDelete(x) {
			var currentRow = x.closest('tr');
			currentId = currentRow.cells[0].getElementsByTagName('input')[0].value;
			if (confirm("Точно хотите удалить запись с id="+currentId+"?")) {
			    // Delete!
				document.location.href="/RoboPortal2/DeleteUser?id="+currentId;
			} else {
			    // Do nothing!
			}
// 			alert("Точно хотите удалить запись с id="+currentId+"?");
		}

	</script>
	
	<script type="text/javascript">
		function checkform() {
			var text = $('#login').val();
			 console.log(text);
			  var obj = "${logins}";
			  var index;
			  //for (index = 0; index < obj.length; ++index) {
			   console.log(obj);
			   var n = obj.indexOf(text);
			   if (n==-1) {
				   console.log("No");
				   document.frmMr.submit();
			   }
			   else {
				   console.log("Yes");
				   var alertWindow=alert("Такой логин уже есть!!");
				   document.getElementById("login").focus();
				   return false;
		    }
		}
	</script>
	<!-- Modal -->
<%//TODO переделать под таблицу %>
		<div id="myModal" class="modal fade" role="dialog" charset="UTF-8">
		  <div class="modal-dialog">
		    <!-- Modal content-->
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Новый пользователь</h4>
		      </div>
		      <form action="AddUser" method="post" accept-charset="UTF-8" name=frmMr>
			      <div class="modal-body">
			        <p>Введите данные</p>
					<label>ФИО:</label><input name="FIO" required placeholder="Иванов Иван Иванович" type="text" size="25" maxlength="25" 
						accept-charset="UTF-8" autofocus><br />
					<label>Группа:</label> 
						<select name="group" required id="groupCreate">
<!-- 							<option selected>student</option> -->
							<c:forEach items="${groupList}" var="group">
								<option value=<c:out value="${group.group}" />><c:out value="${group.group}" /></option>
			    			</c:forEach> 
					 	</select> <br /> 	

					<script type="text/javascript"> 
					    var groupNew=document.getElementById("groupCreate");
					    groupNew.value="student";
					</script>

					<label>e-mail:</label> <input name="email" required pattern="\S+@[a-z]+.[a-z]+" type="email" size="15" 
						maxlength="25"><br />
			        <label>Логин:</label> <input id="login" name="login" type="text" size="15" maxlength="15" required>
<!-- 				        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min.js"></script> -->
						<script>
							$('#login').blur(function() {
							  var text = $(this).val();
							  if (text == "111") {
							    alert("Нельзя начинать с цифры")
							  }
							  var obj = "${logins}";
							  var index;
							  //for (index = 0; index < obj.length; ++index) {
							   console.log(obj);
// 							   var n = obj.indexOf(text);
// 							   if (n==-1) {console.log("No")}
// 							   else {
// 								   console.log("Yes");
// 								   var alertWindow=alert("Такой логин уже есть!!");
//  							  	   document.getElementById("login").focus();
// // 								   alertWindow.close();
// 							   }
							//	}							  
							})
						</script>
						<% //out.print("Hello");  %>
			        <br />
					<label>пароль:</label> <input name="password" type="password" size="15" maxlength="15" required><br />
			      </div>
			      <div class="modal-footer">
			        <input type="button" class="btn btn-default" value="Сохранить" onclick="javascript: checkform()"> <!-- data-dismiss="modal" onclick="javascript: submit()"> -->
			        <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
			      </div>
		      </form>
		    </div>
		
		  </div>
		</div>
	
		<div id="myModalEdit" class="modal" charset="UTF-8">
		<div class="modal-dialog">
		  <!-- Modal content -->
		  <div class="modal-content">
		    <div class="modal-header">
		        <span id="closeEdit" class="close">X</span>
		        <h4 class="modal-title">Изменить пользователя</h4>
		    </div>
		    <div class="modal-body">
				<form action="EditUser" method="post" accept-charset="UTF-8" name=formEdit>
						<input type="hidden" name="userId" id="userId"> 
						<label>ФИО:</label><input name="FIO" required placeholder="Иванов Иван Иванович" type="text" size="25" maxlength="25" 
							accept-charset="UTF-8" autofocus id="FIO"><br />
						<label>Группа:</label> 
							<select name="group" required id="groupEdit">
								<c:forEach items="${groupList}" var="group">
									<option value=<c:out value="${group.group}" />><c:out value="${group.group}" /></option>
				    			</c:forEach> 
<!-- 								<option selected>student</option> -->
						 	</select> <br /> 	
						<label>e-mail:</label> <input name="email" required pattern="\S+@[a-z]+.[a-z]+" type="email" size="15" 
							maxlength="25" id="emailEdit"><br />
						<label>пароль:</label> <input name="password" type="password" size="40" maxlength="30"
							placeholder="Оставьте пустым для сохранения старого"><br />
							
				    <div class="modal-footer">
				        <input type="submit" class="btn btn-default" value="Сохранить"> <!-- data-dismiss="modal" onclick="javascript: submit()"> -->
				        <button type="button" class="btn btn-default" id="closeEditButton">Отмена</button>
				  	</div>  
		    	</form>
		    </div>
		  </div>
		  </div>
		
		</div>
	
	<p>Сохранить в </p>
	
	<form action="ToMSOffice">
		<input type="submit" class="btn btn-default" value="Excel" name="toOffice">
		<input type="submit" class="btn btn-default" value="Word" name="toOffice">
	</form>
	
</body>
</html>