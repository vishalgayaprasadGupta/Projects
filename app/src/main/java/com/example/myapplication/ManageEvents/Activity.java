package com.example.myapplication.ManageEvents;

public class Activity {
    String name, description, date, venue, rules,activityId,availability;

    public String getActivityId() {
        return activityId;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Activity(String name, String description, String venue, String date, String rules,String activityId,String availability) {
        this.name = name;
        this.description = description;
        this.venue = venue;
        this.date = date;
        this.rules = rules;
        this.activityId = activityId;
        this.availability = availability;
    }

    public Activity() {
        // Default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
