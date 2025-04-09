<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="CSS/Game.css" >
<title>Number Guessing Game</title>
</head>
<body>
	<%
        String Message = (String) request.getAttribute("message");
        if (Message!=null){
            out.println("<script>alert('"+Message+" ');</script>");
        } 
   %>
   <div class="Container">
	<h1>Welcome ,${param.PlayerName}</h1>
	<div class="section">
	<form action="GameServlet" method="POST">
		<input type="hidden" name="RandomNumber" value="${RandomNumber}" />
		<input type="hidden" name="PlayerName" value="${PlayerName}" />
		<input type="hidden" name="AttemptsLeft" value="${AttemptsLeft}" />
		<input type="hidden" name="Score" value="${Score}" />
		<div class="A">
			<ul>
				<li> You have to Guess the number between 1 - 100 </li>
				<li> You have Total 10 Attempts </li>
				<li> Total Score is 50 </li>
				<li> For each Wrong Guess -5 </li>
				<li> Bonus of +10 for Successful Completion of Game </li>
			</ul>
		</div>
		<br><br>
		<input type="text" name="GuessNumber" placeholder="Enter a Number " required/>
		<br><br>
		<input type="submit" value="Submit" />
	</form>
	<br><br>
	<h1>Attempts Left  ${AttemptsLeft}</h1>
	</div>
	</div>
</body>
</html>