<%@ page language="java" contentType="text/html;  charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>

<head>
	<link rel="stylesheet"  href="bootstrap.css">
    <link rel="stylesheet" href="main.css">
	<script src="jquery.min.js"></script>
    <script src="bootstrap.min.js"></script>
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
<title>Мои проекты</title>

<script type="text/javascript">
// 	var btn = document.createElement('image');
// 	var btn=document.getElementById('imgBut');
	var btn = document.createElement("img");
	btn.setAttribute("src", "Images/edit.png");
// 	elem.setAttribute("height", "768");
	btn.setAttribute("width", "30");
	btn.setAttribute("alt", "Редактировать");
	btn.id="imgBut";
	btn.title="Редактировать";
	btn.style.cursor='pointer';
</script>

</head>
<body>
<div class="left" >
	Проекты
<!-- 	<input type="image" id="imgBut" src="Images/edit.png" value="editCourse"/> -->
<!-- 	<img alt="" src=""> -->
	<jsp:include page="ShowMyProjects" />
	<br>
	<table	border="0" cellpadding="5" align="left">
		<tr>
			<td>  <br> </td>
		</tr>
		<c:forEach items="${myProjects}" var="projects">
			<tr>
	            <td><a href="#" onClick="javascript: showMyProject(this)"><c:out value="${projects.projectName}" /> </a></td>
	        <td> 
	        </td>
	        </tr>
	            
    	</c:forEach>
<!-- 	<form action="AddProject"> -->
		<tr>
			<td>  <br> </td>
<!-- 			<br> -->
		</tr>
		
		<tr>
		  <td><input type="submit" value="Создать проект" onClick="javascript: showCreateProject()"> </td>
		</tr>  
<!-- 	</form> -->
	</table>
	<script type="text/javascript">
// 		function showCreateProject() {
// 			document.getElementById("rightDiv").innerHTML='<object type="text/html" data="PageConstruct.jsp" ></object>';
// 		}
		function showCreateProject() {
		    $('#rightDiv').load('PageConstruct.jsp');
		}
		function showMyProject(x) {
			var prName=x.innerHTML;
			console.log(prName);
// 			document.location.href="/RoboPortal2/CurrentProject?prName="+prName;
			$('#rightDiv').load('project.jsp?prName='+prName);
			var currentRow = x.closest('tr');
// 			console.log(currentRow
			var td=currentRow.cells[1];
			td.appendChild(btn);
// 				currentRow.cells[1].innerHTML = '<input type="button" value="Редактировать">';
// 			currentId = currentRow.cells[0].getElementsByTagName('input')[0].value;
			btn.onclick = function(){
				console.log('edit:'+prName); 
			};
		}

	</script>
	<%-- 			<%session.setAttribute("projectName", ""); %> --%>
	
</div>
<div class="right" id="rightDiv">
</div>
	
</body>
</html>