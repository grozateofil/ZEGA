package com.gt.zega.entity;

import java.util.ArrayList;

public class Hospital {

    private String hospitalName;
    private Address hospitalAddress;
    private ArrayList<String> hospitalDepartments;

    public Hospital(String hospitalName, Address hospitalAddress, ArrayList<String> hospitalDepartments) {
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.hospitalDepartments = hospitalDepartments;
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

    public ArrayList<String> getHospitalDepartments() {
        return hospitalDepartments;
    }

    public void setHospitalDepartments(ArrayList<String> hospitalDepartments) {
        this.hospitalDepartments = hospitalDepartments;
    }

    @Override
    public String toString() {
        String toString = hospitalName + ", " + hospitalAddress;

        if (hospitalDepartments.size() > 0) {
            toString += ", sectii: " + hospitalDepartments;
        }

        return toString;
    }
}
