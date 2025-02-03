package com.example.myapplication.eventOrganiser;
public class EventOrganiser {
    private String status;
    private String role;
    private String name;
    private String gender;
    private String email;
    private String contact;
    private String college;
    private String password;
    private String stream;
    private String department;
    private String isVerificationEmailsend;
    private String isEmailverified,uid;

    public EventOrganiser(String uid,String status, String role, String name, String gender, String email,
                          String contact, String college, String password, String stream, String department,String isVerificationEmailsend,String isEmailverified) {
        this.status = status;
        this.role = role;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.contact = contact;
        this.college = college;
        this.password = password;
        this.stream = stream;
        this.department = department;
        this.isVerificationEmailsend=isVerificationEmailsend;
        this.isEmailverified=isEmailverified;
        this.uid=uid;
    }
    public EventOrganiser() {
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsEmailverified() {
        return isEmailverified;
    }

    public void setIsEmailverified(String isEmailverified) {
        this.isEmailverified = isEmailverified;
    }

    public String getIsVerificationEmailsend() {
        return isVerificationEmailsend;
    }
    public void setIsVerificationEmailsend(String isVerificationEmailsend) {
        this.isVerificationEmailsend = isVerificationEmailsend;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}