<%@ page language="java" contentType="text/html;  charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<link rel="stylesheet"  href="bootstrap.css">
    <link rel="stylesheet" href="main.css">
	<script src="jquery.min.js"></script>
    <script src="bootstrap.min.js"></script>
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
<title>Конструктор страниц</title>
	<script type="text/javascript">
		var textAreaCount=0;
		var imagesCount=0;
		
	</script>

</head>
<body>
  <div class="constructPage">
	<form action="ProjectCreate" method="post" accept-charset="UTF-8" enctype="multipart/form-data">
		<div id="constArea" > 
			<label>Название проекта</label>
			<input type="text" id="projectName" name="projectName" required>
			<br>
		
		</div>
		<input type="button" value="Добавить текстовую область" onclick="addTextArea()">
			<script type="text/javascript">
				function addTextArea() {
					textAreaCount++;
					console.log("textAreaCount="+textAreaCount);
					var newdiv = document.createElement('div');
					newdiv.className = "elements";
					newdiv.innerHTML = "<br><textarea name='myTextAreas' placeholder='Введите текст...'></textarea>"
					document.getElementById("constArea").appendChild(newdiv);
				}
				
			</script>
		
		<input type="button" value="Добавить изображение" onclick="addImage()">
<!-- 		<input type="file" name ="file" id="fileinput" accept="application/pdf"/> -->
			<script type="text/javascript">
				function addImage() {
					imagesCount++;
					console.log("imagesCount="+imagesCount);
					var newdiv = document.createElement('div');
					newdiv.className = "elements";
					newdiv.innerHTML = "<br><input type='file' name ='file' id='fileinput' accept='image/*'/>"
					document.getElementById("constArea").appendChild(newdiv);
				}
				
			</script>
		
		
		<br>
		<br>
		<input type="submit" value="Сохранить проект">
	</form>
  </div>	

			<script type="text/javascript">
				function sendAll() {
// 					var texts[];
					var texts = document.getElementsByTagName('textarea');
					console.log(texts.length);
					for(var i=0;i<texts.length;i++){
						console.log(texts[i].value);
					}
// 					document.location.href="/RoboPortal2/ProjectCreate?texts[]="+texts; //отправлять строку с порядком следования textarea и картинок
				}
			</script>
			
</body>
</html>