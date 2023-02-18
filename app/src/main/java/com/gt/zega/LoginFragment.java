package com.gt.zega;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Pattern;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout email;
    private TextInputLayout password;
    private TextInputEditText passwordTIET;

    private Button forgotPasswordButton;
    private Button loginButton;
    private Button registerButton;
    private CheckBox rememberMeCheckBox;

    private boolean emailExists;

    private FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.loginEmail);
        password = view.findViewById(R.id.loginPassword);
        passwordTIET = view.findViewById(R.id.loginPasswordTIET);

        passwordTIET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (validation()) {
                        checkIfEmailExists(email.getEditText().getText().toString().replace(" ", ""));
                    }
                }
                return false;
            }
        });

        forgotPasswordButton = view.findViewById(R.id.forgotPassword);
        rememberMeCheckBox = view.findViewById(R.id.rememberMeCheckBox);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.createAccountButton);

        fAuth = FirebaseAuth.getInstance();

        forgotPasswordButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.forgotPassword):
                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.hide(this);
                transaction.add(R.id.content_frame, forgotPasswordFragment);
                transaction.addToBackStack(TAG);
                transaction.commit();
                break;
            case (R.id.loginButton):
                if (validation()) {
                    if (checkIfEmailExists(email.getEditText().getText().toString().replace(" ", ""))) {
                        HomeFragment homeFragment = new HomeFragment();
                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                        fragmentTransaction.hide(this);
                        fragmentTransaction.add(R.id.content_frame, homeFragment);
                        fragmentTransaction.addToBackStack(TAG);
                        fragmentTransaction.commit();
                    }
                }

                break;
            case (R.id.createAccountButton):
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.hide(this);
                fragmentTransaction.add(R.id.content_frame, registerFragment);
                fragmentTransaction.addToBackStack(TAG);
                fragmentTransaction.commit();

                break;
        }
    }

    private boolean validation() {
        return !(!emailValidation(email.getEditText().getText().toString().replace(" ", "")) |
                !passwordValidation(password.getEditText().getText().toString()));
    }

    private boolean emailValidation(String email) {
        String regexPattern = "^(.+)@(\\S+)$";
        boolean isCorrect = Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
        if (email.isEmpty()) {
            this.email.setError(getText(R.string.required));
            return false;
        } else if (!isCorrect) {
            this.email.setError(getText(R.string.invalidEmail));
            return false;
        } else {
            this.email.setError(null);
            this.email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean checkIfEmailExists(String emailAddress) {
        fAuth.fetchSignInMethodsForEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        emailExists = task.getResult().getSignInMethods().isEmpty();
                        if (emailExists) {
//                            Toast.makeText(getActivity().getApplicationContext(), "Nu exista niciun cont asociat acestei adrese de email", Toast.LENGTH_LONG).show();
                            email.setError("Nu există niciun cont asociat acestei adrese de email");
                        } else {
                            email.setError(null);
                            email.setErrorEnabled(false);
                            Toast.makeText(getActivity().getApplicationContext(), "Succes!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return !emailExists;
    }

    private boolean passwordValidation(String password) {
        if (password.isEmpty()) {
            this.password.setError(getText(R.string.required));
            return false;
        } else {
            this.password.setError(null);
            this.password.setErrorEnabled(false);
            return true;
        }
    }
}