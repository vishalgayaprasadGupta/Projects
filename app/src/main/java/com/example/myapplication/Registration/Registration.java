package com.example.myapplication.Registration;

public class Registration {
    private String eventId;
    private String eventName;
    private String activityId;
    private String activityName,uid,studentId,studentName,studentEmail,paymentStatus;

    public Registration(String uid,String studentId,String eventId, String eventName, String activityId, String activityName,String studentName,String studentEmail,String paymentStatus) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.activityId = activityId;
        this.activityName = activityName;
        this.uid=uid;
        this.studentId=studentId;
        this.studentName=studentName;
        this.studentEmail=studentEmail;
        this.paymentStatus=paymentStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
