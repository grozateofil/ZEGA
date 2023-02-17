package com.gt.zega;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Pattern;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout email;
    private TextInputLayout password;

    private Button forgotPasswordButton;
    private Button loginButton;
    private Button registerButton;
    private CheckBox rememberMeCheckBox;

    private boolean emailExists;

    private FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        email = rootView.findViewById(R.id.loginEmail);
        password = rootView.findViewById(R.id.loginPassword);

        forgotPasswordButton = rootView.findViewById(R.id.forgotPassword);
        rememberMeCheckBox = rootView.findViewById(R.id.rememberMeCheckBox);
        loginButton = rootView.findViewById(R.id.loginButton);
        registerButton = rootView.findViewById(R.id.createAccountButton);

        fAuth = FirebaseAuth.getInstance();

        forgotPasswordButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgotPassword:
                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.hide(this);
                transaction.add(R.id.content_frame, forgotPasswordFragment);
                transaction.addToBackStack(TAG);
                transaction.commit();
                break;
            case R.id.loginButton:
                if (validation()) {
                    checkIfEmailExists(email.getEditText().getText().toString());
                }
                break;
            case R.id.createAccountButton:
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
        if (emailValidation(email.getEditText().getText().toString()) == false |
                passwordValidation(password.getEditText().getText().toString()) == false) {
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
            this.email.setError(getText(R.string.required));
            return false;
        } else if (isCorrect == false) {
            this.email.setError(getText(R.string.invalidEmail));
            return false;
        } else {
            this.email.setError(null);
            this.email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean checkIfEmailExists(String email) {
        fAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        emailExists = task.getResult().getSignInMethods().isEmpty();

                        if (emailExists) {
                            Toast.makeText(getActivity().getApplicationContext(), R.string.incorrectEmail, Toast.LENGTH_LONG).show();
                        } else {
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