package com.example.myapplication;
public class Event {
    private String name;
    private String eventId;
    private String eventType;


    public Event() {
        // Firestore needs a no-argument constructor to deserialize the object
    }
    public Event(String name, String eventType) {
        this.name = name;
        this.eventType = eventType;
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
