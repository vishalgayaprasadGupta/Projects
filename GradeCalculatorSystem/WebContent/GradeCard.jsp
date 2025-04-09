<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="CSS/Grade.css" >
<title>GRADECARD</title>
</head>
<body>
<div class="container">
	<h1> STUDENT GRADE CARD </h1>
	<div class="section">
	<table border="4">
		<tr>
			<th>Student Name</th>		
			<th>Student Stream</th>
			<th>Student Roll No</th>
			<th>Student Total Marks</th>
			<th>Student Percentage</th>
			<th>Student Grade</th>
		</tr>
		<tr>
			<td>${StudentName}</td>
			<td>${Stream}</td>
			<td>${Roll}</td>
			<td>${MarksObtained}</td>
		    <td>${Percentage}</td>
		    <td>${Grade}</td>
		</tr>
	</table>
	</div>
	<br>
</div>
</body>
</html>