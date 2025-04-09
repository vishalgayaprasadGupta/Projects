package OnlineReservationSystem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CancellationServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String PNR=request.getParameter("PNR");
		String UserName=request.getParameter("UserName");
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection dbconn=DriverManager.getConnection("jdbc:mysql://localhost:3306/reservationsystem","root","vishal");
			
			PreparedStatement query=dbconn.prepareStatement("DELETE FROM ReservationRecord WHERE PNR=? ");
			query.setString(1, PNR);
			
			int StatusUpdate=query.executeUpdate();
			
			query.close();
			dbconn.close();
			
			if(StatusUpdate > 0) {
				request.setAttribute("message", "success");
			}else {
				request.setAttribute("message", "fail");
			}
			
			RequestDispatcher dispatcher=request.getRequestDispatcher("User.jsp");
			request.setAttribute("UserName",UserName);
			dispatcher.forward(request, response);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
