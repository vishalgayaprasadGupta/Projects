

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ATMServlet extends HttpServlet {
	private double InitialBalance=5000.00; 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BankAccount account = new BankAccount(InitialBalance);
		String Operation=request.getParameter("operation");
		InitialBalance=account.getBalance();
		String nextPage=Operation+".jsp";
		if("CheckBalance".equals(Operation)) {
			request.setAttribute("Message", "CheckBalance");
			request.setAttribute("InitialBalance", InitialBalance);
			RequestDispatcher dispatcher=request.getRequestDispatcher("CheckBalance");
			dispatcher.forward(request, response);
		}else {
			request.setAttribute("InitialBalance", InitialBalance);
			RequestDispatcher dispatcher=request.getRequestDispatcher(nextPage);
			dispatcher.forward(request, response);
		}	
	}
}
