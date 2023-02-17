package com.gt.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout firstname;
    private TextInputLayout lastname;
    private TextInputLayout email;
    private TextInputLayout password;

    private FirebaseAuth fAuth;

    private DatabaseReference databaseReference;

    private boolean emailExists;

    private Button createButton;
    private Button backToLoginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);

        createButton = view.findViewById(R.id.createButton);
        backToLoginButton = view.findViewById(R.id.backToLoginButton);

        fAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        createButton.setOnClickListener(this);
        backToLoginButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createButton:
                if (validation()) {
                    firebaseRegistration2();
                }
                break;
            case R.id.backToLoginButton:
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, loginFragment)
                        .commit();
                break;
        }
    }

    private boolean validation() {
        if (!emailValidation(email.getEditText().getText().toString()) |
                !firstNameValidation(firstname.getEditText().getText().toString()) | !lastNameValidation(lastname.getEditText().getText().toString()) |
                !passwordValidation(password.getEditText().getText().toString())) {
            return false;
        }
        return true;
    }

    private boolean emailValidation(String email) {
        String regexPattern = "^(.+)@(\\S+)$";
        boolean isCorrect = Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
        if (email.isEmpty()) {
            this.email.setError("Camp obligatoriu");
            return false;
        } else if (isCorrect == false) {
            this.email.setError("Email invalid");
            return false;
        } else {
            this.email.setError(null);
            this.email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean lastNameValidation(String lastName) {
        if (lastName.isEmpty()) {
            this.lastname.setError("Camp obligatoriu");
            return false;
        } else {
            this.lastname.setError(null);
            this.lastname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean firstNameValidation(String firstName) {
        if (firstName.isEmpty()) {
            this.firstname.setError("Camp obligatoriu");
            return false;
        } else {
            this.firstname.setError(null);
            this.firstname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean passwordValidation(String password) {
        if (password.isEmpty()) {
            this.password.setError("Camp obligatoriu");
            return false;
        } else {
            this.password.setError(null);
            this.password.setErrorEnabled(false);
            return true;
        }
    }

    private void firebaseRegistration() {
        fAuth.createUserWithEmailAndPassword(email.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnCompleteListener((@NonNull Task<AuthResult> authResultTask) -> {

            if (authResultTask.isSuccessful()) {
                Toast.makeText(getActivity().getApplicationContext(), "Succes", Toast.LENGTH_LONG).show();
            } else {
                fAuth.fetchSignInMethodsForEmail(email.getEditText().getText().toString())
                        .addOnCompleteListener((@NonNull Task<SignInMethodQueryResult> task) -> {

                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                            if (isNewUser) {
                                emailExists = false;
                            } else {
                                emailExists = true;
                            }

//                                }
                        });
                if (emailExists == true) {
                    Toast.makeText(getActivity().getApplicationContext(), "This email exists", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void firebaseRegistration2() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child("Firstname").push().setValue(firstname.getEditText().getText().toString());
                databaseReference.child("Lastname").push().setValue(lastname.getEditText().getText().toString());
                databaseReference.child("Email").push().setValue(email.getEditText().getText().toString());
                databaseReference.child("Password").push().setValue(password.getEditText().getText().toString());
                Toast.makeText(getActivity().getApplicationContext(), "Succes!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}