<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="CSS/Game.css">
<title>Insert title here</title>
</head>
<body>
<div class="Container">
	<%
		String Message=(String)request.getAttribute("message");
		if(Message!=null){
			out.println("<script>alert('"+Message+"')</script>");
		}
	%>
	<div class="section">
	<form action="User.jsp" method="POST">
		<div class="A">
			<h1>Game Score </h1>
		</div>
		<h1>PLAYER NAME: ${PlayerName}</h1>
		<h1>PLAYER SCORE :  ${Score} / 50</h1>
		<input type="submit" value="Start New Game " />
	</form>
	</div>
</div>
</body>
</html>