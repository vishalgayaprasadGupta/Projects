package com.example.myapplication;
public class Event {
    private String name;
    private String eventId;
    private String eventType;
    private String eventStatus;
    private String eventDepartment;
    private String eventStream,startDate,endDate,college;


    public Event() {
        // Firestore needs a no-argument constructor to deserialize the object
    }
    public Event(String name, String eventType,String eventStatus,String eventStream,String eventDepartment,String startDate,String endDate,String college) {
        this.name = name;
        this.eventType = eventType;
        this.eventStatus=eventStatus;
        this.eventStream=eventStream;
        this.eventDepartment=eventDepartment;
        this.startDate=startDate;
        this.endDate=endDate;
        this.college=college;
    }

    public String getCollege() {
        return college;
    }
    public void setCollege(String college) {
        this.college = college;
    }

    public String getEventStream() {
        return eventStream;
    }

    public void setEventStream(String eventStream) {
        this.eventStream = eventStream;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
