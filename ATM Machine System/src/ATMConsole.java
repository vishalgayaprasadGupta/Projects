

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ATMConsole extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		double  InitialBalance=(double) request.getAttribute("InitialBalance");
		String Message=(String) request.getAttribute("Message");
		System.out.println("Message : "+Message);
		
		PrintWriter out=response.getWriter();
		 switch(Message) {
			case "CheckBalance":
				out.println("<h1><script>alert('Your Balance is "+InitialBalance+"');</script></h1>");
				break;
			case "Withdraw":
				out.println("<h1><script>alert('Cash withdraw Successfuly  ,Updated Balance is "+InitialBalance+"');</script></h1>");
				Message=null;
				break;
			case "Deposit":
				out.println("<h1><script>alert('Cash Deposit Successfuly  ,Updated Balance is "+InitialBalance+"');</script></h1>");
				break;
	 	}
		 
		 RequestDispatcher dispatcher=request.getRequestDispatcher("ATM.jsp");
			dispatcher.include(request, response);
		
	}

}
