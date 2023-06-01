package com.gt.zega.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class BrokenMedicalDevicesMonthly implements Serializable {

    private String date;
    private String errorCode;
    private int numberOfBrokenDevices;
    private ArrayList<String> arrayListOfDevicesCodes;

    public BrokenMedicalDevicesMonthly(String errorCode, int numberOfBrokenDevices, ArrayList<String> arrayListOfDevicesCodes) {
        this.errorCode = errorCode;
        this.numberOfBrokenDevices = numberOfBrokenDevices;
        this.arrayListOfDevicesCodes = arrayListOfDevicesCodes;
    }

    public BrokenMedicalDevicesMonthly(String date, String errorCode, int numberOfBrokenDevices, ArrayList<String> arrayListOfDevicesCodes) {
        this.date = date;
        this.errorCode = errorCode;
        this.numberOfBrokenDevices = numberOfBrokenDevices;
        this.arrayListOfDevicesCodes = arrayListOfDevicesCodes;
    }

    public String getDate() {
        return date;
    }

    public String getDayFromDate() {
        return date.split("\\.")[0];
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getNumberOfBrokenDevices() {
        return numberOfBrokenDevices;
    }

    public void setNumberOfBrokenDevices(int numberOfBrokenDevices) {
        this.numberOfBrokenDevices = numberOfBrokenDevices;
    }

    public ArrayList<String> getArrayListOfDevicesCodes() {
        return arrayListOfDevicesCodes;
    }

    public void setArrayListOfDevicesCodes(ArrayList<String> arrayListOfDevicesCodes) {
        this.arrayListOfDevicesCodes = arrayListOfDevicesCodes;
    }

    @Override
    public String toString() {
        return "date: " + date +
                ", errorCode: " + errorCode +
                ", numberOfBrokenDevices: " + numberOfBrokenDevices +
                ", arrayListOfDevicesCodes: " + arrayListOfDevicesCodes;
    }
}
