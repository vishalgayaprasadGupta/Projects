package com.example.myapplication.ManageEvents;

public class Workshop {
    String eventName,workshopTitle,workshopDescription,workshopDate,activityStartTime,activityEndTime;
    String workshopVenue,availability,registrationFees,specialRequirements,activityId,eventId,eventType,activityType;

    public Workshop(String eventName,String workshopTitle, String workshopDescription, String workshopDate,
                    String workshopVenue, String availability, String registrationFees, String specialRequirements,
                    String eventId,String eventType,String activityType,String activityStartTime,String activityEndTime) {
        this.workshopTitle = workshopTitle;
        this.workshopDescription = workshopDescription;
        this.workshopDate = workshopDate;
        this.workshopVenue = workshopVenue;
        this.availability = availability;
        this.registrationFees = registrationFees;
        this.specialRequirements = specialRequirements;
        this.eventId=eventId;
        this.eventType=eventType;
        this.activityType=activityType;
        this.eventName=eventName;
        this.activityStartTime=activityStartTime;
        this.activityEndTime=activityEndTime;
    }

    public String getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(String activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public String getActivityEndTime() {
        return activityEndTime;
    }

    public void setActivityEndTime(String activityEndTime) {
        this.activityEndTime = activityEndTime;
    }

    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
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

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
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
