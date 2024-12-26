package com.example.myapplication;
public class Event {
    private String name;
    private String description;
    private String date;
    private String time;
    private String location;
    private String eventId;


    public Event() {
        // Firestore needs a no-argument constructor to deserialize the object
    }
    public Event(String name, String description, String date,String eventId) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }
}
