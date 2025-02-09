package com.example.myapplication.ManageEvents;

public class Activity {
    String activtiyName, activtiyDescription, activtiyVenue, activtiyDate, activtiyRules, availability, eventId, activityId, registrationFee, eventType, activityType, eventName;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String activtiyAvailability) {
        this.availability = activtiyAvailability;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Activity(String eventName, String activtiyName, String activtiyDescription, String activtiyVenue, String activtiyDate, String activtiyRules, String availability, String eventId, String registrationFee, String eventType, String activityType) {
        this.activtiyName = activtiyName;
        this.activtiyDescription = activtiyDescription;
        this.activtiyVenue = activtiyVenue;
        this.activtiyDate = activtiyDate;
        this.activtiyRules = activtiyRules;
        this.eventId = eventId;
        this.availability = availability;
        this.registrationFee = registrationFee;
        this.eventType = eventType;
        this.activityType = activityType;
        this.eventName = eventName;
    }


    public void setRegistrationFee(String registrationFee) {
        this.registrationFee = registrationFee;
    }

    public Activity() {
        // Default constructor
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

    public String getActivtiyDate() {
        return activtiyDate;
    }

    public void setActivtiyDate(String activtiyDate) {
        this.activtiyDate = activtiyDate;
    }

    public String getActivtiyRules() {
        return activtiyRules;
    }

    public void setActivtiyRules(String activtiyRules) {
        this.activtiyRules = activtiyRules;
    }

    public String getRegistrationFee() {
        return registrationFee;
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
}