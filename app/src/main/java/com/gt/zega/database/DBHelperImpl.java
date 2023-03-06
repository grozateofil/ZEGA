package com.gt.zega.database;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.gt.zega.entity.User;

public class DBHelperImpl implements DBHelper {

    private User user;

    public User readUserFromDB(FirebaseUser firebaseUser, DatabaseReference databaseReference) {
        user = new User();
        if (firebaseUser != null) {
            String userKey = firebaseUser.getUid();
            databaseReference.child("users").child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    setUser(dataSnapshot.getValue(User.class));
//                user = dataSnapshot.getValue(User.class);
                    System.out.println("----------------------------------------------------->" + user.toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }
        return user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
