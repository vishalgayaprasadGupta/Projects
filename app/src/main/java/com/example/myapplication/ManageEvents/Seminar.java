package com.example.myapplication.ManageEvents;

import com.google.firebase.firestore.Exclude;
public class Seminar {
    String activityName,activtyDescription,activtiyDate,activtiyVenue,availability,eventId,activtiyDuration,
            eventName,selectedStartTime,selectedEndTime,status,id;
    String speakerName,speakerBio,registrationFee,seminarAgenda,activityId,eventType,specialRequirements,activityType,activityTime;

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

    public Seminar(String eventName,String activityName, String activtyDescription, String activtiyDate, String activtiyVenue
                 , String speakerName, String speakerBio, String registrationFee,
                   String seminarAgenda,String eventId,String eventType,String specialRequirements,String availability
                  ,String activityStartTime,String activityEndTime,String status) {
        this.activityName = activityName;
        this.activtyDescription = activtyDescription;
        this.activtiyDate = activtiyDate;
        this.activtiyVenue = activtiyVenue;
        this.speakerName = speakerName;
        this.speakerBio = speakerBio;
        this.registrationFee = registrationFee;
        this.seminarAgenda = seminarAgenda;
        this.eventId=eventId;
        this.eventType=eventType;
        this.specialRequirements=specialRequirements;
        this.availability=availability;
        this.eventName=eventName;
        this.selectedStartTime=activityStartTime;
        this.selectedEndTime=activityEndTime;
        this.status=status;
    }
    public Seminar() {
    }

    @Exclude
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getActivityStartTime() {
        return selectedStartTime;
    }
    public void setActivityStartTime(String activityStartTime) {
        this.selectedStartTime = activityStartTime;
    }

    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getActivityEndTime() {
        return selectedEndTime;
    }
    public void setActivityEndTime(String activityEndTime) {
        this.selectedEndTime = activityEndTime;
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

    public String getActivtiyDate() {
        return activtiyDate;
    }

    public void setActivtiyDate(String activtiyDate) {
        this.activtiyDate = activtiyDate;
    }

    public String getSeminarVenue() {
        return activtiyVenue;
    }

    public void setSeminarVenue(String activtiyVenue) {
        this.activtiyVenue = activtiyVenue;
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

    public String getregistrationFee() {
        return registrationFee;
    }

    public void setregistrationFee(String registrationFee) {
        this.registrationFee = registrationFee;
    }

    public String getSeminarAgenda() {
        return seminarAgenda;
    }

    public void setSeminarAgenda(String seminarAgenda) {
        this.seminarAgenda = seminarAgenda;
    }
}
