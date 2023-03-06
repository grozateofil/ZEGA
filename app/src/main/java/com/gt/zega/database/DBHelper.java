package com.gt.zega.database;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.gt.zega.entity.User;

public interface DBHelper {

    public User readUserFromDB(FirebaseUser firebaseUser, DatabaseReference databaseReference);

    public User getUser();

}
