package com.gt.zega.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gt.zega.R;
import com.gt.zega.entity.User;

public class HomeFragment extends Fragment {

    private TextView hello;
    private ProgressBar progressBar;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private String role;
    private String firstname;
    private String lastname;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        hello = view.findViewById(R.id.hello);
        progressBar = view.findViewById(R.id.progress_bar_home);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        if (firebaseUser != null) {
            getUserData(firebaseUser.getUid());
        } else {
            hello.setText("Buna!");
        }
        return view;

    }

    private void getUserData(String userKey) {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    String userRole = user.getRole();
                    role = userRole;
                    firstname = user.getFirstName();
                    lastname = user.getLastName();
                    progressBar.setVisibility(View.GONE);
                    hello.setText("Bună, " + role.toUpperCase() + " " + firstname + " " + lastname + "!");
                } else {
                    //TODO sa iasa automat din cont
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                hello.setText(error.getMessage());
            }
        });
    }

}