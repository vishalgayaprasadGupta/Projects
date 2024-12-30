package com.example.myapplication.ManageEvents;

public class Activity {
    String name, description, date, venue, rules,eventId,availability,activityId,registrationFee,eventType;
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

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Activity(String name, String description, String venue, String date, String rules,String availability,String eventId,String registrationFee,String eventType) {
        this.name = name;
        this.description = description;
        this.venue = venue;
        this.date = date;
        this.rules = rules;
        this.eventId = eventId;
        this.availability = availability;
        this.registrationFee = registrationFee;
        this.eventType = eventType;
    }

    public String getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(String registrationFee) {
        this.registrationFee = registrationFee;
    }

    public Activity() {
        // Default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
