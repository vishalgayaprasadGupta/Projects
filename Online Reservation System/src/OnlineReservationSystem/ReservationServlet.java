package OnlineReservationSystem;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReservationServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String UserName=request.getParameter("UserName");
		//System.out.println("Username: " + UserName);          #Used for  Debugging#S
		String TrainNumber=request.getParameter("trainNumber");
		String TrainName=request.getParameter("TrainName");
		String SourceStation=request.getParameter("Source");
		String DestinationStation=request.getParameter("Destination");
		String ClassType=request.getParameter("Class");
		String JourneyDate=request.getParameter("date");
		String PassengerName=request.getParameter("name");
		String Age=request.getParameter("age");
		String Gender=request.getParameter("gender");
		String PNR = generatePNR();
		
		
		boolean Record=passengerRecord(UserName,PNR,TrainNumber,TrainName,SourceStation,DestinationStation,ClassType,JourneyDate,PassengerName,Age,Gender);
		PrintWriter out=response.getWriter();
		
		if (Record) {
		    request.setAttribute("reservationStatus", "success");
		    request.setAttribute("UserName",UserName);
		    request.setAttribute("PNR", PNR);
		} else {
		    request.setAttribute("reservationStatus", "fail");
		    request.setAttribute("UserName",UserName);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("User.jsp");
		dispatcher.forward(request, response);
		out.close();
	}

	    
	public boolean passengerRecord(String UserName,String PNR ,String TrainNumber,String TrainName,String SourceStation,String DestinationStation,String ClassType,String JourneyDate,String PassengerName,String Age,String Gender) {
		String Status="CNF";
		Date BookingDate = new Date(System.currentTimeMillis());
		
		try {
			
		  Class.forName("com.mysql.jdbc.Driver"); 
		  Connection dbconn=DriverManager.getConnection("jdbc:mysql://localhost:3306/reservationsystem","root","vishal");
		 PreparedStatement query=dbconn.prepareStatement("Insert into ReservationRecord(UserName,PNR,TrainNumber,TrainName,Source,Destination,Class,JourneyDate,PassengerName,PassengerAge,PassengerGender,ReservationStatus,BookingDate) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		 
		 	query.setString(1, UserName);
		 	query.setString(2, PNR);                
		    query.setString(3, TrainNumber);         
		    query.setString(4, TrainName);       
		    query.setString(5, SourceStation);   
		    query.setString(6, DestinationStation);
		    query.setString(7, ClassType);       
		    query.setString(8, JourneyDate);     
		    query.setString(9, PassengerName); 
		    query.setString(10, Age);   
		    query.setString(11, Gender);
		    query.setString(12, Status);
		    query.setDate(13, BookingDate);
		    
		    System.out.println("Username: " +UserName);
		    query.executeUpdate();
		    query.close();
		    dbconn.close();
	 }catch(Exception ex) {
		 ex.printStackTrace();
		 return false;
	 }
	return true;
	}
	  private String generatePNR() {
	        Random random = new Random();
	        StringBuilder pnr = new StringBuilder(10);

	        for (int i = 0; i < 10; i++) {
	            pnr.append(random.nextInt(10));
	        }

	        return pnr.toString();
	    }
  }
