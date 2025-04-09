<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>CHECK PNR STATUS</title>
<link rel="stylesheet" type="text/css" href="CSS/BookingDetails.css">
</head>
<body>
<div class="Container">
    <header>
        <h1> PASSENGER PNR STATUS </h1>
    </header>
     <div class="section">
    <form action="PNRStatus" method="POST">
		ENTER PNR : <input type="text" name="PNR"  required/><br><br>
		<input type="submit" value="CHECK STATUS" /><br><br>
	</form>
	
        <c:choose>
            <c:when test="${not empty reservations}">
            <div class="form">
                <table border="2">
                        <tr>
                            <th>PNR</th>
                            <th>Train Number</th>
                            <th>Train Name</th>
                            <th>Source</th>
                            <th>Destination</th>
                            <th>Class</th>
                            <th>Journey Date</th>
                            <th>Passenger Name</th>
                            <th>Passenger Age</th>
                            <th>Passenger Gender</th>
                            <th>Reservation Status </th>
                            <th>Booking Date </th>
                        </tr>
                    <tbody>
                        <c:forEach var="reservation" items="${reservations}">
                            <tr>
                                <td>${reservation.PNR}</td>
                                <td>${reservation.trainNumber}</td>
                                <td>${reservation.trainName}</td>
                                <td>${reservation.source}</td>
                                <td>${reservation.destination}</td>
                                <td>${reservation.classType}</td>
                                <td>${reservation.journeyDate}</td>
                                <td>${reservation.passengerName}</td>
                                <td>${reservation.passengerAge}</td>
                                <td>${reservation.passengerGender}</td>
                                <td>${reservation.reservationStatus}</td>
                                <td>${reservation.bookingDate}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                 </div>
            </c:when>
            <c:otherwise>
                <p>No reservations found.</p>
            </c:otherwise>
        </c:choose>
        <br><br>
    </div>
</div>
</body>
</html>
