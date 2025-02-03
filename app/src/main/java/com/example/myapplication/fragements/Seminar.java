package com.example.myapplication.fragements;

public class Seminar {
    String activityName,activtyDescription,activtiyDate,activtiyVenue,availability,eventId,activtiyDuration,eventName;
    String speakerName,speakerBio,registrationFee,seminarAgenda,activityId,eventType,specialRequirements,activityType;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Seminar(String eventName,String activityName, String activtyDescription, String activtiyDate, String activtiyVenue, String activtiyDuration, String speakerName, String speakerBio, String registrationFee, String seminarAgenda,String eventId,String eventType,String specialRequirements,String availability,String activityType) {
        this.activityName = activityName;
        this.activtyDescription = activtyDescription;
        this.activtiyDate = activtiyDate;
        this.activtiyVenue = activtiyVenue;
        this.activtiyDuration = activtiyDuration;
        this.speakerName = speakerName;
        this.speakerBio = speakerBio;
        this.registrationFee = registrationFee;
        this.seminarAgenda = seminarAgenda;
        this.eventId=eventId;
        this.eventType=eventType;
        this.specialRequirements=specialRequirements;
        this.availability=availability;
        this.activityType=activityType;
        this.eventName=eventName;
    }
    public Seminar() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSeminarTitle() {
        return activityName;
    }

    public void setSeminarTitle(String activityName) {
        this.activityName = activityName;
    }

    public String getSeminarDescription() {
        return activtyDescription;
    }

    public void setSeminarDescription(String activtyDescription) {
        this.activtyDescription = activtyDescription;
    }

    public String getSeminarDate() {
        return activtiyDate;
    }

    public void setSeminarDate(String activtiyDate) {
        this.activtiyDate = activtiyDate;
    }

    public String getSeminarVenue() {
        return activtiyVenue;
    }

    public void setSeminarVenue(String activtiyVenue) {
        this.activtiyVenue = activtiyVenue;
    }

    public String getSeminarDuration() {
        return activtiyDuration;
    }

    public void setSeminarDuration(String activtiyDuration) {
        this.activtiyDuration = activtiyDuration;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public String getSpeakerBio() {
        return speakerBio;
    }

    public void setSpeakerBio(String speakerBio) {
        this.speakerBio = speakerBio;
    }

    public String getRegistrationFeeSeminar() {
        return registrationFee;
    }

    public void setRegistrationFeeSeminar(String registrationFeeSeminar) {
        this.registrationFee = registrationFee;
    }

    public String getSeminarAgenda() {
        return seminarAgenda;
    }

    public void setSeminarAgenda(String seminarAgenda) {
        this.seminarAgenda = seminarAgenda;
    }
}
