

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CheckBalance extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		double InitialBalance=(Double) request.getAttribute("InitialBalance");
		System.out.println("jkd:"+InitialBalance);
		InitialBalance=checkbalance(InitialBalance);

		request.setAttribute("Message", "CheckBalance");
		request.setAttribute("InitialBalance", InitialBalance);
		
		RequestDispatcher dispatcher=request.getRequestDispatcher("ATMConsole");
		dispatcher.forward(request, response);
	}
	
	public double checkbalance(double InitialBalance) {
		return InitialBalance;
	}
	

}
