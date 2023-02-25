package com.gt.zega.database;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Checking {

    private static boolean emailExists;

    private static boolean wasVerified = false;

    public static boolean checkIfEmailExists(FirebaseAuth fAuth, TextInputLayout email, String errorMessage) {

        fAuth.fetchSignInMethodsForEmail(email.getEditText().getText().toString().replace(" ", ""))
                .addOnCompleteListener(task -> {
                    if (!task.getResult().getSignInMethods().isEmpty()) {
                        emailExists = true;
                        email.setError(null);
                        email.setErrorEnabled(false);

                    } else {
                        emailExists = false;
                        email.setError(errorMessage);
                    }
                });
        return emailExists;
    }


    //TODO 1. dupa înregistrarea unui nou user dacă încerc sa ma conectez la cont (inainte de a confirma adresa de email) aplicația crapă
    //TODO 2. dupa confirmare trebuie sa apas de 2 ori pe butonul de "CONECTEAZA-TE" ca sa pot intra in cont
    public static boolean emailVerification(FirebaseAuth fAuth, Context context) {


        if (fAuth.getCurrentUser() != null) {
            fAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    final FirebaseUser firebaseUser = fAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        boolean isEmailVerified = firebaseUser.isEmailVerified();
                        if (isEmailVerified) {
                            wasVerified = true;

                        } else {
                            wasVerified = false;
                            Toast.makeText(context, "Nu ați confirmat adresa de email", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Eroare! Contactați echipa de suport.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
        return wasVerified;
    }

}
