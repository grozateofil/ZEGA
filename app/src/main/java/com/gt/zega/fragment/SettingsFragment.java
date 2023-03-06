package com.gt.zega.fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gt.zega.R;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Button deleteAccountButton;
    private SharedPreferences sharedPreferences;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = getContext().getSharedPreferences("Preferences", 0);
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        deleteAccountButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.deleteAccountButton): {
                confirmDeleteAccount();
                break;
            }
        }
    }

    private void confirmDeleteAccount() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.delete_account_confirmation, null);
        builder.setView(view);

        Button cancelButton = view.findViewById(R.id.noButton);
        Button okButton = view.findViewById(R.id.yesButton);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        cancelButton.setOnClickListener(view1 -> dialog.cancel());

        okButton.setOnClickListener(view2 -> {
            dialog.cancel();
            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseAuth.getInstance().signOut();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("LOGIN");
            editor.apply();

            LoginFragment loginFragment = new LoginFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, loginFragment).commit();

            firebaseUser.delete().addOnSuccessListener(unused -> {
                System.out.println("---------------------------------------------->" + "Authentication user deleted");

                databaseReference.child(uid).removeValue().addOnSuccessListener(aVoid -> {
                    System.out.println("---------------------------------------------->" + "Realtime database deleted");

                }).addOnFailureListener(e -> {
                    System.out.println("---------------------------------------------->" + "Realtime database error:  " + e.getMessage());
                });

            }).addOnFailureListener(e -> {
                System.out.println("---------------------------------------------->" + "Authentication user error: " + e.getMessage());
            });

        });

    }

}