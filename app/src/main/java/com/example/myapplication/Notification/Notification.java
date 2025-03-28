package com.example.myapplication.Notification;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Notification {
    private String title;
    private String message;
    private String senderType;
    boolean seen;

    @ServerTimestamp
    private Date timestamp;

    public Notification() {
        // Default constructor required for Firestore
    }

    public Notification(String title, String message, String senderType, Date timestamp) {
        this.title = title;
        this.message = message;
        this.senderType = senderType;
        this.timestamp = timestamp;
    }

    public Boolean getSeen() {
        return seen;
    }
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
