package com.example.myapplication.eventOrganiser;
public class EventOrganiser {
    private String status;
    private String role;
    private String username;
    private String gender;
    private String email;
    private String phone;
    private String college;
    private String password;
    private String stream;      // Add stream field
    private String department;  // Add department field

    public EventOrganiser(String status, String role, String username, String gender, String email,
                          String phone, String college, String password, String stream, String department) {
        this.status = status;
        this.role = role;
        this.username = username;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.college = college;
        this.password = password;
        this.stream = stream;
        this.department = department;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
