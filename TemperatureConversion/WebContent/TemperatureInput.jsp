<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="CSS/Temperature.css" >
<title>Temperature Converter</title>
</head>
<body>
<div class="container">
	<header>
		<h1>TEMPERATURE CONVERSION SYSTEM</h1>
	</header>
	<div class="section">
	<form action="TemperatureServlet" method="POST">
		Enter Temperature Value : <input type="text" name="Temp" required >
		Unit : <select id="Unit" name="Unit">
					<option disable selected>SELECT</option>
					<option>CELCIUS</option>
					<option>FAREHNITE</option>
					<option>KELVIN</option>
				</select>
				<br><br>
		<input type="submit" value="Convert" />
	</form>
	<br><br>
	<div class="form">
	<form>
		CELCIUS : <input type="text" value="${CelciusTemp}" readonly/><br><br>
		FAREHNITE : <input type="text" value="${FrehniteTemp}" readonly/><br><br>
		KELVIN : <input type="text" value="${KelvinTemp}" readonly/><br><br>
	</form>
	</div>
	</div>
</div>
</body>
</html>