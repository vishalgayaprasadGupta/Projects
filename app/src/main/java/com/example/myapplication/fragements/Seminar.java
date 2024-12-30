package com.example.myapplication.fragements;

public class Seminar {
    String seminarTitle,seminarDescription,seminarDate,seminarVenue,seminarDuration;
    String speakerName,speakerBio,registrationFeeSeminar,seminarAgenda,activityId,eventType,eventId,specialRequirements,availability;

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

    public Seminar(String seminarTitle, String seminarDescription, String seminarDate, String seminarVenue, String seminarDuration, String speakerName, String speakerBio, String registrationFeeSeminar, String seminarAgenda,String eventId,String eventType,String specialRequirements,String availability) {
        this.seminarTitle = seminarTitle;
        this.seminarDescription = seminarDescription;
        this.seminarDate = seminarDate;
        this.seminarVenue = seminarVenue;
        this.seminarDuration = seminarDuration;
        this.speakerName = speakerName;
        this.speakerBio = speakerBio;
        this.registrationFeeSeminar = registrationFeeSeminar;
        this.seminarAgenda = seminarAgenda;
        this.eventId=eventId;
        this.eventType=eventType;
        this.specialRequirements=specialRequirements;
        this.availability=availability;
    }
    public Seminar() {
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
        return seminarTitle;
    }

    public void setSeminarTitle(String seminarTitle) {
        this.seminarTitle = seminarTitle;
    }

    public String getSeminarDescription() {
        return seminarDescription;
    }

    public void setSeminarDescription(String seminarDescription) {
        this.seminarDescription = seminarDescription;
    }

    public String getSeminarDate() {
        return seminarDate;
    }

    public void setSeminarDate(String seminarDate) {
        this.seminarDate = seminarDate;
    }

    public String getSeminarVenue() {
        return seminarVenue;
    }

    public void setSeminarVenue(String seminarVenue) {
        this.seminarVenue = seminarVenue;
    }

    public String getSeminarDuration() {
        return seminarDuration;
    }

    public void setSeminarDuration(String seminarDuration) {
        this.seminarDuration = seminarDuration;
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
        return registrationFeeSeminar;
    }

    public void setRegistrationFeeSeminar(String registrationFeeSeminar) {
        this.registrationFeeSeminar = registrationFeeSeminar;
    }

    public String getSeminarAgenda() {
        return seminarAgenda;
    }

    public void setSeminarAgenda(String seminarAgenda) {
        this.seminarAgenda = seminarAgenda;
    }
}
