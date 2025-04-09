package OnlineReservationSystem;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegistrationServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String Username=request.getParameter("uname");
		String Password=request.getParameter("upass");
		String Email=request.getParameter("email");
		boolean details=InsertintoDatabase(Username,Password,Email);
		PrintWriter out=response.getWriter();
		if(details){
			out.write("<h1><script>alert('Registration Succesfull ,Redirecting to Login Page');</script> </h1>");
			 out.write("<h1><script>window.location.href = 'Login.html';</script></h1>"); 
		}else {
			out.write("<h1><script>alert('Account Already Exists with entered details. Try using a different Username or Email.');</script><h1>");
			 out.write("<h1><script>window.location.href = 'Registration.html';</script></h1>"); 
		}
		
	}
	public boolean InsertintoDatabase(String Username,String Password,String Email) {
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection dbconn=DriverManager.getConnection("jdbc:mysql://localhost:3306/reservationsystem","root","vishal");
			
			PreparedStatement check=dbconn.prepareStatement("Select count(*) from USerData Where username=? OR email=? ");
			check.setString(1,Username);
			check.setString(2,Email);
			ResultSet result=check.executeQuery();
			result.next();
			int count=result.getInt(1);
			if(count==0) {
				Statement query=dbconn.createStatement();
				query.execute("Insert into UserData(username,password,email) values('"+Username+"','"+Password+"','"+Email+"')");
				
				dbconn.close();
				query.close();
			}else {
				return false;
			}
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
	}
	


}
