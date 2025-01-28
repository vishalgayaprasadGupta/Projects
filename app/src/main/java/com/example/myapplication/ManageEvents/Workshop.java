package com.example.myapplication.ManageEvents;

public class Workshop {
    String workshopTitle,workshopDescription,workshopDate;
    String workshopVenue,availability,registrationFees,specialRequirements,activityId,eventId,eventType;

    public Workshop(String workshopTitle, String workshopDescription, String workshopDate, String workshopVenue, String availability, String registrationFees, String specialRequirements,String eventId,String eventType) {
        this.workshopTitle = workshopTitle;
        this.workshopDescription = workshopDescription;
        this.workshopDate = workshopDate;
        this.workshopVenue = workshopVenue;
        this.availability = availability;
        this.registrationFees = registrationFees;
        this.specialRequirements = specialRequirements;
        this.eventId=eventId;
        this.eventType=eventType;
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Workshop() {
        // Default constructor
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getWorkshopTitle() {
        return workshopTitle;
    }

    public void setWorkshopTitle(String workshopTitle) {
        this.workshopTitle = workshopTitle;
    }

    public String getWorkshopDescription() {
        return workshopDescription;
    }

    public void setWorkshopDescription(String workshopDescription) {
        this.workshopDescription = workshopDescription;
    }

    public String getWorkshopDate() {
        return workshopDate;
    }

    public void setWorkshopDate(String workshopDate) {
        this.workshopDate = workshopDate;
    }

    public String getWorkshopVenue() {
        return workshopVenue;
    }

    public void setWorkshopVenue(String workshopVenue) {
        this.workshopVenue = workshopVenue;
    }

    public String getavailability() {
        return availability;
    }

    public void setavailability(String availability) {
        this.availability = availability;
    }

    public String getRegistrationFees() {
        return registrationFees;
    }

    public void setRegistrationFees(String registrationFees) {
        this.registrationFees = registrationFees;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }
}
