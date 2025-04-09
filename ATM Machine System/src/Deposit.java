

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Deposit extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String inputBalance=request.getParameter("InitialBalance");
		double InitialBalance=Double.parseDouble(inputBalance);
		String inputAmount=request.getParameter("amount");
		double Amount=Integer.parseInt(inputAmount);
		
		double NewBalance=deposit(InitialBalance,Amount);
		
		request.setAttribute("Message", "Deposit");
		request.setAttribute("Amount", Amount);
		request.setAttribute("InitialBalance", NewBalance);
		
		RequestDispatcher dispatcher=request.getRequestDispatcher("ATMConsole");
		dispatcher.forward(request, response);
	}
	
	public double deposit(double InitialBalance,double Amount) {
			double NewBalance=InitialBalance+Amount;
		return NewBalance;
	}
	

}
