package OnlineReservationSystem;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String Username=request.getParameter("uname");
		String Password=request.getParameter("upass");
		PrintWriter out=response.getWriter();
		boolean details=autorization(Username,Password);
		int userid=Userid(Username,Password);
		String username=UserName(Username,Password);
		if(details) {
			RequestDispatcher dispatcher=request.getRequestDispatcher("User.jsp");
			String UserName =username;
			request.setAttribute("UserName", UserName);
			int id=userid;
			request.setAttribute("id", id);
			dispatcher.forward(request,response);
			
		}else {
			out.write("<script>alert('Invalid Credentials! , UserName and Password are Case and Space Sensitive  ')</script>");
			RequestDispatcher dispatcher=request.getRequestDispatcher("Login.html");
			dispatcher.include(request, response);
		}
		
		
	}
	public boolean autorization(String Username,String Password) {
		boolean isvalid=false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection dbconn=DriverManager.getConnection("jdbc:mysql://localhost:3306/reservationsystem","root","vishal");
			PreparedStatement query=dbconn.prepareStatement("select count(*) from UserData where username=? and password=?");
			query.setString(1,Username);
			query.setString(2,Password);
			ResultSet result=query.executeQuery();
			while(result.next()){
				int count=result.getInt(1);
				if(count==1){
					isvalid=true;
					break;
				}
				dbconn.close();
				query.close();
			}
		}catch(Exception ex) {
			isvalid=false;
		}
		return isvalid;
	}
	
	public int Userid(String Username,String Password){
		int userId=-1;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection dbconn=DriverManager.getConnection("jdbc:mysql://localhost:3306/reservationsystem","root","vishal");
			PreparedStatement query=dbconn.prepareStatement("select id from UserData where username=? and password=?");
			query.setString(1,Username);
			query.setString(2,Password);
			  ResultSet result = query.executeQuery();
			  	
		        if (result.next()) {
		           userId = result.getInt("id");
		        }
		        result.close();
		        query.close();
		        dbconn.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return userId;
	}
	
	public String UserName(String Username,String Password){
		String UserName=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection dbconn=DriverManager.getConnection("jdbc:mysql://localhost:3306/reservationsystem","root","vishal");
			PreparedStatement query=dbconn.prepareStatement("select username from UserData where username=? and password=?");
			query.setString(1,Username);
			query.setString(2,Password);
			  ResultSet result = query.executeQuery();
			  	
			  if (result.next()) {
				    UserName = result.getString("username");
		        }
		        result.close();
		        query.close();
		        dbconn.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return UserName;

}
}
