package com.gt.zega.entity;

import java.util.ArrayList;

public class User {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String hospitalName;
    private ArrayList<String> hospitalDepartmentsNames;
    private String role;
    private boolean blockedAccount;

    public User() {

    }

    public User(String firstName, String lastName, String phoneNumber, String hospitalName, ArrayList<String> hospitalDepartmentsNames, String role, boolean blockedAccount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.hospitalName = hospitalName;
        this.hospitalDepartmentsNames = hospitalDepartmentsNames;
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

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public ArrayList<String> getHospitalDepartmentsNames() {
        return hospitalDepartmentsNames;
    }

    public void setHospitalDepartmentsNames(ArrayList<String> hospitalDepartmentsNames) {
        this.hospitalDepartmentsNames = hospitalDepartmentsNames;
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
                ", hospital: " + hospitalName +
                ", hospitalSection: " + hospitalDepartmentsNames +
                "role: " + role;
    }


}
