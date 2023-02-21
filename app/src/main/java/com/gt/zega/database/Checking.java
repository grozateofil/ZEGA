package com.gt.zega.database;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Checking {

    private static boolean emailExists;

    public static boolean checkIfEmailExists(FirebaseAuth fAuth, TextInputLayout email, String errorMessage) {
        fAuth.fetchSignInMethodsForEmail(email.getEditText().getText().toString().replace(" ", ""))
                .addOnCompleteListener(task -> {
                    emailExists = task.getResult().getSignInMethods().isEmpty();
                    if (emailExists) {
                        email.setError(errorMessage);
                    } else {
                        email.setError(null);
                        email.setErrorEnabled(false);
                    }

                });
        return emailExists;
    }
}
