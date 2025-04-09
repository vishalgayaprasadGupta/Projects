package TemperatureConversionSystem;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemperatureServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tempinput=request.getParameter("Temp");
		double TemperatureValue=Double.parseDouble(tempinput);
		String Unit=request.getParameter("Unit");

		double CelciusTemp=0;
		double FrehniteTemp=0;
		double KelvinTemp=0;
		
		switch(Unit) {
			case "CELCIUS":
				FrehniteTemp=CelciusToFarehnite(TemperatureValue);
				KelvinTemp=CelciusToKelvin(TemperatureValue);
				request.setAttribute("CelciusTemp", TemperatureValue);
				request.setAttribute("FrehniteTemp", FrehniteTemp);
				request.setAttribute("KelvinTemp", KelvinTemp);
				break;
			case "FAREHNITE":
				CelciusTemp=FarehniteToCelcius(TemperatureValue);
				KelvinTemp=FarehniteToKelvin(TemperatureValue);
				request.setAttribute("FrehniteTemp", TemperatureValue);
				request.setAttribute("CelciusTemp", CelciusTemp);
				request.setAttribute("KelvinTemp", KelvinTemp);
				break;
			case "KELVIN":
				CelciusTemp=KelvinToCelcius(TemperatureValue);
				FrehniteTemp=KelvinToFarehnite(TemperatureValue);
				request.setAttribute("KelvinTemp", TemperatureValue);
				request.setAttribute("CelciusTemp", CelciusTemp);
				request.setAttribute("FrehniteTemp", FrehniteTemp);
				break;
		}
		
		RequestDispatcher dispatcher=request.getRequestDispatcher("TemperatureInput.jsp");
		dispatcher.forward(request, response);
		
	}
	
	public double CelciusToFarehnite(double TemperatureValue) {
		double Result=0;
		Result=(TemperatureValue * 9/5)+32;
		return Result;
	}
	public double CelciusToKelvin(double TemperatureValue) {
		double Result=0;
		Result=TemperatureValue+273.15;
		return Result;
	}
	public double FarehniteToCelcius(double TemperatureValue) {
		double Result=0;
		Result=(TemperatureValue-32)*5/9 ;
		return Result;
	} 
	public double FarehniteToKelvin(double TemperatureValue) {
		double Result=0;
		Result=(TemperatureValue-32)*5/9 +273.15 ;
		return Result;
	}
	public double KelvinToCelcius(double TemperatureValue) {
		double Result=0;
		Result=TemperatureValue-273.15 ;
		return Result;
	}
	public double KelvinToFarehnite(double TemperatureValue) {
		double Result=0;
		Result=(TemperatureValue-273.15) * 9/5 + 32;
		return Result;
	}
}
