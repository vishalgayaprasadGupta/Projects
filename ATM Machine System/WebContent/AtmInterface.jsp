<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="style.css" >
<title>ATM</title>
</head>
<body>
<div class="container">
	<h1>ATM BANKING </h1>
	<div class="section">
	<form action="ATMServlet" method="POST" >

		<br><br>
		DEPOSIT : <input type="radio" name="operation" value="Deposit" /><br><br>
		WITHDRAW : <input type="radio" name="operation" value="Withdraw" /><br><br>
		CHECK-BALANCE : <input type="radio" name="operation" value="CheckBalance" /><br><br>
		<br><br>
		<input type="submit" value="SUBMIT" />
	</form>
	</div>
</div>
	</body>
</html>