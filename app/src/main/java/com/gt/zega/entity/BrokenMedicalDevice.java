package com.gt.zega.entity;

import java.io.Serializable;

public class BrokenMedicalDevice implements Serializable {

    private String problemName;
    private String year;
    private String month;
    private String day;
    private String deviceCode;

    public BrokenMedicalDevice(String problemName, String year, String month, String day, String deviceCode) {
        this.problemName = problemName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.deviceCode = deviceCode;
    }

    public BrokenMedicalDevice(String year, String month) {
        this.problemName = problemName;
        this.year = year;
        this.month = month;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    @Override
    public String toString() {
        return problemName + ", " + year + ", " + month + ", " + day + ", " + deviceCode;
    }
}
