package com.gt.zega.entity;

public class User {

    private String role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String imageUrl;
    private UserAccount userAccount;
    private boolean blockedAccount;

    public User() {

    }


    public User(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
    }

    public User(String firstName, String lastName, String phoneNumber, String role) {
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;

    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
        return "User: " + "role: " + role + ", firstName: " + firstName + ", lastName: " + lastName + ", phoneNumber: " + phoneNumber + ", imageUrl: " + imageUrl + ", userAccount: " + userAccount;
    }
}
