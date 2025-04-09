package NumberGuessingGame;

import java.io.IOException;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GameServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int MaxAttempt=10;
		String PlayerName=request.getParameter("PlayerName");
		String round=request.getParameter("round");
		int AttemptsLeft=MaxAttempt;
		int RandomNumber;
		String Gameover=null;;
		String Alert=null;
		int WrongAttempt=5;
		int Score=50;
		int UserGuess=0;
		try {
		if("NewGame".equals(round)){
			Random random=new Random();
			RandomNumber=random.nextInt(100)+1;
			AttemptsLeft=MaxAttempt;
		}else {
			 RandomNumber=Integer.parseInt(request.getParameter("RandomNumber"));
			 AttemptsLeft=Integer.parseInt(request.getParameter("AttemptsLeft"));
		
			String input=request.getParameter("GuessNumber");
			UserGuess=Integer.parseInt(input);
			String score=request.getParameter("Score");
			Score=Integer.parseInt(score);
			if(UserGuess==RandomNumber) {
				Alert="Congratulations "+PlayerName+" You have Guess the Right number" ;
				Score+=10;
				Gameover="finish";
			}else if(UserGuess>RandomNumber){
				Alert="Wrong Number! ,Enter Number is higher than the Generated Numbrer ";
				AttemptsLeft--;
				Score-=WrongAttempt;
			}else if(UserGuess<RandomNumber){
				Alert="Enter Number! is Lesser than the Generated Numbrer ";
				AttemptsLeft--;
				Score-=WrongAttempt;
			}
			
			if(AttemptsLeft<=0) {
				Alert="Game OVER! You have used all Attempts. The correct number was "+RandomNumber ;
				Gameover="finish";
			}	
	}
		request.setAttribute("Score", Score);
		if("finish".equals(Gameover)) {
			request.setAttribute("message", Alert);
			request.setAttribute("PlayerName", PlayerName);
			request.setAttribute("Score", Score);
			RequestDispatcher dispatcher=request.getRequestDispatcher("Result.jsp");
			dispatcher.forward(request, response);		
		}else {
			request.setAttribute("UserGuess", UserGuess);
			request.setAttribute("message", Alert);
			request.setAttribute("PlayerName", PlayerName);
			request.setAttribute("AttemptsLeft", AttemptsLeft);
			request.setAttribute("RandomNumber", RandomNumber);
			RequestDispatcher dispatcher=request.getRequestDispatcher("Game.jsp");
			dispatcher.forward(request, response);
		}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
  }	
}
