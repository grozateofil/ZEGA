package com.gt.zega.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class BrokenMedicalDevicesMonthly implements Serializable {

    private String date;
    private ArrayList<String> errorCode;
    private int numberOfBrokenDevices;
    private ArrayList<String> arrayListOfDevicesCodes;

    public BrokenMedicalDevicesMonthly(ArrayList<String> errorCode, int numberOfBrokenDevices, ArrayList<String> arrayListOfDevicesCodes) {
        this.errorCode = errorCode;
        this.numberOfBrokenDevices = numberOfBrokenDevices;
        this.arrayListOfDevicesCodes = arrayListOfDevicesCodes;
    }

    public BrokenMedicalDevicesMonthly(String date, ArrayList<String> errorCode, int numberOfBrokenDevices, ArrayList<String> arrayListOfDevicesCodes) {
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

    public ArrayList<String> getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ArrayList<String> errorCode) {
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
        String toString = "Data: " + date;
        if (!errorCode.isEmpty()) {
            if (errorCode.size() == 1)
                toString += "\nCod eroare: " + errorCode;
            else
                toString += "\nCoduri erori: " + errorCode;
        }
        if (numberOfBrokenDevices >= 0)
            toString += "\nNumar de defectiuni: " + numberOfBrokenDevices;
        if (!arrayListOfDevicesCodes.isEmpty()) {
            if (arrayListOfDevicesCodes.size() == 1)
                toString += "\nAparat: " + arrayListOfDevicesCodes;
            else
                toString += "\nAparate: " + arrayListOfDevicesCodes;
        }
        return toString;
    }
}
