package com.example.myapplication.ManageEvents;

public class InterCollege {
    String activitytName, activityDescription, activityVenue, activityRules, availability, registrationFee, eventId, eventType, activityDate, activityId;

    public InterCollege(String activitytName, String activityDescription, String activityVenue, String activityDate, String activityRules, String availability, String registrationFee, String eventId, String eventType) {
        this.activitytName = activitytName;
        this.activityDescription = activityDescription;
        this.activityVenue = activityVenue;
        this.activityDate = activityDate;
        this.activityRules = activityRules;
        this.availability = availability;
        this.registrationFee = registrationFee;
        this.eventId = eventId;
        this.eventType = eventType;

    }

    public String getActivitytName() {
        return activitytName;
    }

    public void setActivitytName(String activitytName) {
        this.activitytName = activitytName;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getActivityVenue() {
        return activityVenue;
    }

    public void setActivityVenue(String activityVenue) {
        this.activityVenue = activityVenue;
    }

    public String getActivityRules() {
        return activityRules;
    }

    public void setActivityRules(String activityRules) {
        this.activityRules = activityRules;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(String registrationFee) {
        this.registrationFee = registrationFee;
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

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public InterCollege() {
        // Default constructor
    }

}