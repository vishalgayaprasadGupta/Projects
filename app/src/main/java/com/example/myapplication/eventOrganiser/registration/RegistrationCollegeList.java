package com.example.myapplication.eventOrganiser.registration;

public class RegistrationCollegeList {
    private String eventName;
    private String eventType;
    private String eventStatus;
    private String eventId;

    public RegistrationCollegeList( String eventId,String eventName, String eventType, String eventStatus) {
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventStatus = eventStatus;
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}