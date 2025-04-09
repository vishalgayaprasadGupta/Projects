<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>MY RESERVATIONS</title>
<link rel="stylesheet" type="text/css" href="CSS/BookingDetails.css">
</head>
<body>
  <%
        String reservationStatus = (String) request.getAttribute("message");
        if ("success".equals(reservationStatus)) {
            out.println("<script>alert('Booking Cancelled Succesfully ');</script>");
        } else if ("fail".equals(reservationStatus)) {
            out.println("<script>alert('Error in Cancelling booking! ');</script>");
        }
   %>
<div class="Container">
    <header>
        <h1> PASSENGER RESERVATIONS </h1>
    </header>
     <div class="section">
        <c:choose>
            <c:when test="${not empty reservations}">
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
                            <th>Cancel Booking</th>
                            
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
                                <td>
                                	<form action="CancellationServlet" method="POST">
                                		<input type="hidden" name="UserName" value="${UserName}" />
                                		<input type="hidden" name="PNR" value="${reservation.PNR}" />
                                		<input type="submit" value="CANCEL" />
                                	</form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>No reservations found.</p>
            </c:otherwise>
        </c:choose><br><br>
    </div>
</div>
</body>
</html>
