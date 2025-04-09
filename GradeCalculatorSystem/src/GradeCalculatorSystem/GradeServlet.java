package GradeCalculatorSystem;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GradeServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String StudentName=request.getParameter("StudentName");
		String Stream=request.getParameter("Stream");
		String Roll=request.getParameter("roll");
		
		String input1=request.getParameter("sub1");
		double Subject1=Double.parseDouble(input1);
		
		String input2=request.getParameter("sub2");
		double Subject2=Double.parseDouble(input2);
		
		String input3=request.getParameter("sub3");
		double Subject3=Double.parseDouble(input3);
		
		String input4=request.getParameter("sub4");
		double Subject4=Double.parseDouble(input4);
		
		String input5=request.getParameter("sub5");
		double Subject5=Double.parseDouble(input5);
		                                                                   
		String input6=request.getParameter("sub6");
		double Subject6=Double.parseDouble(input6);

		double TotalMarks=600;
		String Grade="";
		double MarksObtained=Subject1+Subject2+Subject3+Subject4+Subject5+Subject6;
		Double Percentage=(MarksObtained / TotalMarks ) * 100 ;
		
		if(Percentage>=95) {
			Grade="O";
		}else if(Percentage>=85) {
			Grade="A+";
		}else if(Percentage>=80) {
			Grade="A";
		}else if(Percentage>=70) {
			Grade="B+";
		}else if(Percentage>=55) {
			Grade="B";
		}else if(Percentage>=35) {
			Grade="C";
		}else {
			Grade="F";
		}
		
		request.setAttribute("MarksObtained", MarksObtained);
		request.setAttribute("Stream", Stream);
		request.setAttribute("Roll", Roll);
		request.setAttribute("StudentName", StudentName);
		request.setAttribute("Percentage", Percentage);
		request.setAttribute("Grade", Grade);
		RequestDispatcher dispatcher=request.getRequestDispatcher("GradeCard.jsp");
		dispatcher.forward(request, response);
		
		
	}

}
