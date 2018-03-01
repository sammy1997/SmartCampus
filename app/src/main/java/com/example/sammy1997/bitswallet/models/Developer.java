package com.example.sammy1997.bitswallet.models;

/**
 * Created by sammy1997 on 28/2/18.
 */

public class Developer {
    String name;
    String dept;
    String role;
    String phoneNumber;
    String mailId;
    String imageUrl;

    public Developer() {
    }

    public Developer(String name, String dept, String role, String phoneNumber, String mailId, String imageUrl) {
        this.name = name;
        this.dept = dept;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.mailId = mailId;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }
}
