package OnlineReservationSystem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TrainData extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String trainNumber = request.getParameter("trainNumber");
        String UserName=request.getParameter("Username");
        String trainName = null;
       // System.out.println("UserName :"+UserName);		# Used for  Debugging Process ;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection dbconn = DriverManager.getConnection("jdbc:mysql://localhost:3306/reservationsystem", "root", "vishal");

            PreparedStatement query = dbconn.prepareStatement("SELECT TrainName FROM TrainData WHERE TrainNumber=?");
            query.setString(1, trainNumber);
            ResultSet result = query.executeQuery();
            if (result.next()) {
            	trainName = result.getString("TrainName");
            } else {
            	trainName = "Train number not found.";
            }

            result.close();
            query.close();
            dbconn.close();
        } catch (Exception e) {
            e.printStackTrace();
            trainName = "Error occurred while looking up train name.";
        }

        // System.out.println("Name :"+trainName);			# Used for  Debugging Process ;
        RequestDispatcher dispatcher = request.getRequestDispatcher("Reservation.jsp");
        request.setAttribute("trainName", trainName);
        request.setAttribute("trainNumber", trainNumber);
        request.setAttribute("UserName", UserName);
        dispatcher.forward(request, response);
    }
}
