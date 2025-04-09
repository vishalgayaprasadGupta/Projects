<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="CSS/Game.css" >
<title>Insert title here</title>
</head>
<body>
	<div class="Container">
	<h1>WELCOME TO RANDOM NUMBER GUESSING GAME</h1>
	<div class="section">
	<br><br>
	<form action="GameServlet" method="POST" >
		<input type="hidden" value="NewGame" name="round" />
		 Enter Your Name : <input type="text" name="PlayerName" placeholder="Player Name"  required/>
		<br><br><br>
		<input type="submit" value="Start Game" />
	</form>
	</div>
	</div>
</body>
</html>