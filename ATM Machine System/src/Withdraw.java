

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Withdraw extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String inputBalance=request.getParameter("InitialBalance");
		double InitialBalance=Double.parseDouble(inputBalance);
		String inputAmount=request.getParameter("amount");
		double Amount=Integer.parseInt(inputAmount);
		
		double NewBalance=withdraw(InitialBalance,Amount);
		
		String Message="Withdraw";
		
		RequestDispatcher dispatcher=request.getRequestDispatcher("ATMConsole");
		request.setAttribute("Message", Message);
		request.setAttribute("Amount", Amount);
		request.setAttribute("InitialBalance", NewBalance);
		dispatcher.forward(request, response);
	}
	
	public double withdraw(double InitialBalance,double Amount) {
		double NewBalance=0;
		if(Amount<InitialBalance) {
			NewBalance=InitialBalance-Amount;
		}
		return NewBalance;
	}
	

}
