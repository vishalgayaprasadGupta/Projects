package com.example.myapplication;
public class Event {
    private String name;
    private String eventId;
    private String eventType;
    private String eventStatus;


    public Event() {
        // Firestore needs a no-argument constructor to deserialize the object
    }
    public Event(String name, String eventType,String eventStatus) {
        this.name = name;
        this.eventType = eventType;
        this.eventStatus=eventStatus;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
