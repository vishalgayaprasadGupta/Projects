package OnlineReservationSystem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyReservations extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		 String UserName = request.getParameter("username");
		 System.out.println("Username: " + UserName);
	        List<Reservation> reservations = new ArrayList<>();

		 try {
	            Class.forName("com.mysql.jdbc.Driver");

	            Connection dbconn = DriverManager.getConnection("jdbc:mysql://localhost:3306/reservationsystem", "root", "vishal");

	            PreparedStatement query = dbconn.prepareStatement("SELECT * FROM ReservationRecord WHERE UserName=?");
	            query.setString(1, UserName);
	            ResultSet rs = query.executeQuery();
	            System.out.println("Username: " + UserName);

	            while (rs.next()) {
	                Reservation reservation = new Reservation();
	                reservation.setPNR(rs.getString("PNR"));
	                reservation.setTrainNumber(rs.getString("TrainNumber"));
	                reservation.setTrainName(rs.getString("TrainName"));
	                reservation.setSource(rs.getString("Source"));
	                reservation.setDestination(rs.getString("Destination"));
	                reservation.setClassType(rs.getString("Class"));
	                reservation.setJourneyDate(rs.getDate("JourneyDate"));
	                reservation.setPassengerName(rs.getString("PassengerName"));
	                reservation.setPassengerAge(rs.getString("PassengerAge"));
	                reservation.setPassengerGender(rs.getString("PassengerGender"));
	                reservation.setReservationStatus(rs.getString("ReservationStatus"));
	                reservation.setBookingDate(rs.getDate("BookingDate"));
	                
	                reservations.add(reservation);
	                
	            }
	            
	            request.setAttribute("reservations", reservations);
	            RequestDispatcher dispatcher = request.getRequestDispatcher("MyReservations.jsp");
	        	request.setAttribute("UserName",UserName);
	            dispatcher.forward(request, response);
	            
	            rs.close();
	            query.close();
	            dbconn.close();

	        } catch (Exception ex) {
	            ex.printStackTrace();
	            throw new ServletException("Error retrieving reservations", ex);
	        }
	}

}
