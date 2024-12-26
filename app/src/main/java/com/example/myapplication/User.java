package com.example.myapplication;

public class User {
    private String role;
    private String name;
    private String gender;
    private String email;
    private String contact;
    private String college;
    private String password;

    public User(String role, String name, String gender, String email, String contact, String college, String password) {
        this.role = role;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.contact = contact;
        this.college = college;
        this.password = password;
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
}