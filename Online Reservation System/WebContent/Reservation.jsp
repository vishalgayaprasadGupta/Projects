<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="CSS/Reservation.css" >
<script>
function checkstation() {
    var fromStation = document.getElementById('fromStation').value;
    var toStation = document.getElementById('toStation').value;

    if (fromStation == toStation) {
        alert("Source and Destination can't be same .");
        return false; 
    }
    return true; 
}
</script>
<title>RESERVATION PAGE</title>
</head>
<body>
<div class="Container" >
<div class="section">
<br>

	<form action="TrainData" method="POST">
		<input type="hidden" name="Username" value="${param.username}" />
		ENTER TRAIN NUMBER : <input type="text" name="trainNumber" value="${param.trainNumber}" required /><br>
		<input type="submit" value="CHECK TRAIN" /><br>
	</form>
	<div class="form">
	<br><br><br>
	<form action="ReservationServlet" method="POST" onsubmit="return checkstation()">
		<input type="hidden" name="trainNumber" value="${trainNumber}" />
		
		<input type="hidden" name="UserName" value="${UserName}" />
		
		<input type="text" value="${trainName}" name="TrainName" placeholder=" TRAIN NAME " readonly/><br><br>
		SELECT STATION : <br> 
		SOURCE : <select id="fromStation" name="Source" required>
		 	    <option value="" disabled selected>SELECT</option>
				<option value="Hyderabad">Hyderabad </option>
				<option value="Banglore">Banglore</option>
				<option value="Mumbai CST">Mumbai CST</option>
				<option value="Mumbai LTT">Mumbai LTT</option>
				<option value="Thane">Thane</option>
				<option value="Kalyan">Kalyan</option>
				<option value="Bhusaval">Bhusaval</option>
				<option value="Pune">Pune</option>
				<option value="Pryagraj">Pryagraj</option>
				<option value="DDU">DDU</option>
			 </select>
		DESTINATION : <select id="toStation" name="Destination" required>
				<option value="" disabled selected>SELECT</option>
		    	<option value="Hyderabad">Hyderabad </option>
				<option value="Banglore">Banglore</option>
				<option value="Mumbai CST">Mumbai CST</option>
				<option value="Mumbai LTT">Mumbai LTT</option>
				<option value="Thane">Thane</option>
				<option value="Kalyan">Kalyan</option>
				<option value="Bhusaval">Bhusaval</option>
				<option value="Pune">Pune</option>
				<option value="Pryagraj">Prayagraj</option>
				<option value="DDU">DDU</option>
			 </select>
		<br><br>
			SELECT CLASS: <select name="Class" required>
							<option value="2S">2S</option>
							<option value="Sleeper">Sleeper</option>
							<option value="Ac 2 Tier">Ac 2 Tier</option>
							<option value="Economy">Economy</option>
					 	  </select>	
		<br><br>
		JOURNEY DATE : <input type="date" name="date" required/>
		<br><br>
		PASSENGER NAME : <input type="text"  name="name" required/><br><br>
		AGE :             <input type="text" name="age" required/><br><br>
		GENDER        : <input type="radio" value="Male" name="gender" required/>Male  <input type="radio" value="Female" name="gender" />Female<br><br><br><br>
		<br>			<input type="submit" value="CONFIRM RESERVATION" />
	</form>
	</div>
	</div>
</div>
</body>
</html>

