package com.example.myapplication;
public class Event {
    private String name;
    private String eventId;
    private String eventType;
    private String eventStatus;
    private String eventDepartment;
    private String selectStream;


    public Event() {
        // Firestore needs a no-argument constructor to deserialize the object
    }
    public Event(String name, String eventType,String eventStatus,String selectStream,String eventDepartment) {
        this.name = name;
        this.eventType = eventType;
        this.eventStatus=eventStatus;
        this.selectStream=selectStream;
        this.eventDepartment=eventDepartment;
    }

    public String getSelectStream() {
        return selectStream;
    }

    public void setSelectStream(String selectStream) {
        this.selectStream = selectStream;
    }

    public String getEventDepartment() {
        return eventDepartment;
    }

    public void setEventDepartment(String eventDepartment) {
        this.eventDepartment = eventDepartment;
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
