<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="CSS/Grade.css" >
<title>Enter Marks</title>
</head>
<body>
<div class="container">
	<h1> ENTER STUDENT MARKS </h1><br>
	<div class="section">
	<form action="GradeServlet" method="POST">
		<input type="hidden" name="StudentName" value="${param.StudentName}" />
		<input type="hidden" name="Stream" value="${param.Stream}" />
		<input type="hidden" name="roll" value="${param.roll}" />
		Subject1 : <input type="text" name="sub1" placeholder="Enter Marks" required/> / 100<br><br>	
		Subject2 : <input type="text" name="sub2" placeholder="Enter Marks" required/> / 100<br><br>		
		Subject3 : <input type="text" name="sub3" placeholder="Enter Marks" required/> / 100<br><br>		
		Subject4 : <input type="text" name="sub4" placeholder="Enter Marks" required/> / 100<br><br>		
		Subject5 : <input type="text" name="sub5" placeholder="Enter Marks" required/> / 100<br><br>		
		Subject6 : <input type="text" name="sub6" placeholder="Enter Marks" required/> / 100<br><br>	
		<input type="submit" value=" Calculate Grade "	/>
	</form>
	</div>
</div>
</body>
</html>