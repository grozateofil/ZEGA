package com.gt.zega.entity;

import java.util.ArrayList;

public class Hospital {

    private String hospitalName;
    private Address hospitalAddress;
    private ArrayList<String> hospitalSections;

    public Hospital(String hospitalName, Address hospitalAddress, ArrayList<String> hospitalSections) {
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.hospitalSections = hospitalSections;
    }

    public Hospital() {
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public Address getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(Address hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public ArrayList<String> getHospitalSections() {
        return hospitalSections;
    }

    public void setHospitalSections(ArrayList<String> hospitalSections) {
        this.hospitalSections = hospitalSections;
    }

    @Override
    public String toString() {
        String toString = hospitalName + ", " + hospitalAddress;

        if (hospitalSections.size() > 0) {
            toString += ", sectii: " + hospitalSections;
        }

        return toString;
    }
}
