package com.gt.zega.entity;

import java.util.ArrayList;

public class User {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    //    private String imageUrl;
    private UserAccount userAccount;
    private String hospitalName;
    private ArrayList<String> hospitalSections;
    private String role;
    private boolean blockedAccount;

    public User() {

    }


//    public User(String firstName, String lastName, String phoneNumber) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phoneNumber = phoneNumber;
////        this.imageUrl = imageUrl;
//    }
//
//    public User(String firstName, String lastName, String phoneNumber, String role) {
//        this.role = role;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phoneNumber = phoneNumber;
//    }

    public User(String firstName, String lastName, String phoneNumber, String hospitalName, ArrayList<String> hospitalSections, String role, boolean blockedAccount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.userAccount = userAccount;
        this.hospitalName = hospitalName;
        this.hospitalSections = hospitalSections;
        this.role = role;
        this.blockedAccount = blockedAccount;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public ArrayList<String> getHospitalSections() {
        return hospitalSections;
    }

    public void setHospitalSections(ArrayList<String> hospitalSections) {
        this.hospitalSections = hospitalSections;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isBlockedAccount() {
        return blockedAccount;
    }

    public void setBlockedAccount(boolean blockedAccount) {
        this.blockedAccount = blockedAccount;
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "User: " + ", firstName: " + firstName +
                ", lastName: " + lastName +
                ", phoneNumber: " + phoneNumber +
                ", userAccount: " + userAccount +
                ", hospital: " + hospitalName +
                ", hospitalSection: " + hospitalSections +
                "role: " + role;
    }


}
