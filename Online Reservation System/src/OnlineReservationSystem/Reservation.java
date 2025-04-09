package OnlineReservationSystem;

import java.util.Date;

public class Reservation {
    private String PNR;
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private String classType;
    private Date journeyDate;
    private String passengerName;
    private String passengerAge;
    private String passengerGender;
    private String ReservationStatus;
    private Date BookingDate;
    
    
	public String getReservationStatus() {
		return ReservationStatus;
	}
	public void setReservationStatus(String reservationStatus) {
		ReservationStatus = reservationStatus;
	}
	public Date getBookingDate() {
		return BookingDate;
	}
	public void setBookingDate(Date bookingDate) {
		BookingDate = bookingDate;
	}
	public String getPNR() {
		return PNR;
	}
	public void setPNR(String pNR) {
		PNR = pNR;
	}
	public String getTrainNumber() {
		return trainNumber;
	}
	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	public String getTrainName() {
		return trainName;
	}
	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getClassType() {
		return classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}
	public Date getJourneyDate() {
		return journeyDate;
	}
	public void setJourneyDate(Date journeyDate) {
		this.journeyDate = journeyDate;
	}
	public String getPassengerName() {
		return passengerName;
	}
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
	public String getPassengerAge() {
		return passengerAge;
	}
	public void setPassengerAge(String passengerAge) {
		this.passengerAge = passengerAge;
	}
	public String getPassengerGender() {
		return passengerGender;
	}
	public void setPassengerGender(String passengerGender) {
		this.passengerGender = passengerGender;
	}
    
    
    
}