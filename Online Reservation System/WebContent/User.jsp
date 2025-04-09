<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.io.PrintWriter"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css" href="CSS/user.css" >
<title>User Profile</title>
</head>
<body>
  <%	
        String reservationStatus = (String) request.getAttribute("reservationStatus");
		 String PNR=(String)request.getAttribute("PNR");
        if (reservationStatus!=null) {
            out.println("<script>alert('RESERVATION CONFIRMED WITH PNR : "+PNR+" ');</script>");
            request.removeAttribute("reservationStatus");
        } else if ("fail".equals(reservationStatus)) {
            out.println("<script>alert('RESERVATION FAILED , Enter Appropriate Details');</script>");
            request.removeAttribute("reservationStatus");
        }
   %>
   
    <%
        String Message = (String) request.getAttribute("message");
        if (Message!=null) {
            out.println("<script>alert('Booking Cancelled Succesfully ');</script>");
            request.removeAttribute("Message");
        } else if ("fail".equals(Message)) {
            out.println("<script>alert('Error in Cancelling booking! ');</script>");
            request.removeAttribute("Message");
        }
   %>
   
<div class="container">
	<header>
		<h1>Welcome ,${UserName} </h1>
		<form action="Logout" method="GET" >
		<div class="L"> 
		&nbsp;&nbsp;<input type="submit" value="LOGOUT" />
		</div>
		</form>
		
	</header>

	<div class="section">

	 <form action="Reservation.jsp" method="POST">
	 	<div class="A">
	 	<h1>  BOOK RESERVATIONS </h1>
	 	<input type="hidden" name="username" value="${UserName}" />
	    <input type="hidden" name="id" value="${id}" />
	 	<input type="submit" value="BOOK RESERVATION" />
	 	 </div>
	 </form>
	
	
	  <form action="PNRStatus.jsp" method="POST">
	   <div class="B">
	 	<h1>CHECK PNR STATUS </h1>
	 	<input type="hidden" name="UserName" value="${UserName}" />
	 	<input type="hidden" name="id" value="${id}" />
	 	<input type="submit" value="CANCEL RESERVATION" />
	 	 </div>
	 </form>
	
	  <form action="MyReservations" method="POST">
	  <div class="C">
	 	<h1>MY RESERVATIONS </h1>
	 	<input type="hidden" name="username" value="${UserName}" />
	 	<input type="hidden" name="name" value="${name}" />
	 	<input type="hidden" name="id" value="${id}" />
	 	<input type="submit" value="MY RESERVATION" />
	 	 </div>
	 </form>
	 <br>
	 
	</div>
	
</div>
</body>
</html>

