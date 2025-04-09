<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="style.css" >
<title>WITHDRAW</title>
</head>
<body>
<div class="container">
	<h1>Deposit</h1>
	<div class="section">
	<br>
	<br>
	<form action="Withdraw" method="POST">
		<input type="hidden" name="InitialBalance" value="${InitialBalance}" />
		Enter amount to be withdraw : <input type="text" name="amount" required />
		<br><br><br>
		<input type="submit" value="Deposit" />
	</form>
	</div>
</div>
</body>
</html>