package com.gt.zega.entity;

import java.util.ArrayList;

public class UserFiles {

    private String uid;
    private User user;
    private ArrayList<String> listOfFiles;

    public UserFiles(String uid, User user, ArrayList<String> listOfFiles) {
        this.uid = uid;
        this.user = user;
        this.listOfFiles = listOfFiles;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<String> getListOfFiles() {
        return listOfFiles;
    }

    public void setListOfFiles(ArrayList<String> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

}
