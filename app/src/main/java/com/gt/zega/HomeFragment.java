package com.gt.zega;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gt.zega.entity.User;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private TextView userName;

    private Button logout;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userName = view.findViewById(R.id.userName);

        logout = view.findViewById(R.id.logOutButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userKey = firebaseUser.getUid();

        databaseReference.child("users").child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                User user = new User(firstName, lastName, phoneNumber);
                userName.setText("Bună, " + user.getFirstName() + " " + user.getLastName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        sharedPreferences = getContext().getSharedPreferences("Preferences", 0);

        logout.setOnClickListener(this);
        return view;

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.logOutButton):
//                AlertDialog.Builder builder = getDialog();
//
//                builder.show();
                getDialog();
                break;
        }
    }

    private void getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.confirm_logout, null);
        builder.setView(view);

        Button cancelButton = view.findViewById(R.id.btn_No);
        Button okButton = view.findViewById(R.id.btn_Yes);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, loginFragment)
                        .commit();

                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("LOGIN");
                editor.commit();

            }
        });

//        builder.setMessage(R.string.wantToExit)
//                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        LoginFragment loginFragment = new LoginFragment();
//                        FragmentManager fragmentManager = getParentFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.content_frame, loginFragment)
//                                .commit();
//
//                        FirebaseAuth.getInstance().signOut();
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.remove("LOGIN");
//                        editor.commit();
//                    }
//                })
//                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        return builder;
    }


}