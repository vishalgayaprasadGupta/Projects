package com.example.myapplication.ManageEvents;

public class Activity {
    String activtiyName, activtiyDescription, activtiyVenue, activityDate, activtiyRules, availability, eventId,
            activityId, registrationFee, eventType, activityType, eventName, activityStartTime,activityEndTime;

    public Activity() {
    }

    public Activity(String eventName,String activtiyName, String activtiyDescription, String activtiyVenue, String activityDate,
                    String activtiyRules, String availability, String eventId,String registrationFee,String eventType,
                    String activityType, String activityStartTime, String activityEndTime) {
        this.activtiyName = activtiyName;
        this.activtiyDescription = activtiyDescription;
        this.activtiyVenue = activtiyVenue;
        this.activityDate = activityDate;
        this.activtiyRules = activtiyRules;
        this.availability = availability;
        this.eventId = eventId;
        this.activityId = activityId;
        this.registrationFee = registrationFee;
        this.eventType = eventType;
        this.activityType = activityType;
        this.eventName = eventName;
        this.activityStartTime = activityStartTime;
        this.activityEndTime = activityEndTime;
    }

    public String getActivtiyName() {
        return activtiyName;
    }
    public void setActivtiyName(String activtiyName) {
        this.activtiyName = activtiyName;
    }

    public String getActivtiyDescription() {
        return activtiyDescription;
    }

    public void setActivtiyDescription(String activtiyDescription) {
        this.activtiyDescription = activtiyDescription;
    }

    public String getActivtiyVenue() {
        return activtiyVenue;
    }

    public void setActivtiyVenue(String activtiyVenue) {
        this.activtiyVenue = activtiyVenue;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivtiyRules() {
        return activtiyRules;
    }

    public void setActivtiyRules(String activtiyRules) {
        this.activtiyRules = activtiyRules;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(String registrationFee) {
        this.registrationFee = registrationFee;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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
}