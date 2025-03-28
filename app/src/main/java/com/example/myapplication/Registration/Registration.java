package com.example.myapplication.Registration;

public class Registration {
    private String eventName;
    private String activityId,activityDate,activityTime,isPresent;
    int paymentAmount;
    boolean isSelected;
    private String activityName,uid,studentId,participantName,participantEmail,paymentStatus,transactionId;

    public Registration(String uid,String studentId, String eventName, String activityId, String activityName,
                        String participantName,String participantEmail,String paymentStatus,String activityDate,String activityTime,int paymentAmount,String isPresent,String transactionId) {
        this.eventName = eventName;
        this.activityId = activityId;
        this.activityName = activityName;
        this.uid=uid;
        this.studentId=studentId;
        this.participantName=participantName;
        this.participantEmail=participantEmail;
        this.paymentStatus=paymentStatus;
        this.activityDate=activityDate;
        this.activityTime=activityTime;
        this.paymentAmount=paymentAmount;
        this.isPresent=isPresent;
        this.transactionId=transactionId;
    }

    public Registration() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getIsPresent() {
        return isPresent;
    }
    public void setIsPresent(String isPresent) {
        this.isPresent = isPresent;
    }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
