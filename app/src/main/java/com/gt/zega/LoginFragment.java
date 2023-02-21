package com.gt.zega;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout email;
    private TextInputLayout password;

    private Button forgotPasswordButton;
    private Button loginButton;
    private Button registerButton;
    private CheckBox rememberMeCheckBox;

    private FirebaseAuth fAuth;

    private SharedPreferences sharedPreferences;

    private Validations validations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        email = view.findViewById(R.id.loginEmail);
        password = view.findViewById(R.id.loginPassword);

        forgotPasswordButton = view.findViewById(R.id.forgotPasswordButton);
        rememberMeCheckBox = view.findViewById(R.id.rememberMeCheckBox);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.createAccountButton);

        fAuth = FirebaseAuth.getInstance();

        validations = new ValidationsImpl();

        forgotPasswordButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.loginButton):
                if (validation()) {
                    firebaseLogin();
                }
                break;

            case (R.id.forgotPasswordButton):
                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.hide(this);
                transaction.add(R.id.content_frame, forgotPasswordFragment);
                transaction.addToBackStack(TAG);
                transaction.commit();
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
        return !(!validations.emailValidation(email) |
                !validations.passwordValidation(password));
    }

    private void firebaseLogin() {
        String emailAddress = email.getEditText().getText().toString().replaceAll("\\s", "");
        String pass = password.getEditText().getText().toString();

        fAuth.signInWithEmailAndPassword(emailAddress, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    email.setError(null);
                    email.setErrorEnabled(false);

                    password.setError(null);
                    password.setErrorEnabled(false);

                    Toast.makeText(getActivity().getApplicationContext(), "Succes!", Toast.LENGTH_SHORT).show();
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, homeFragment)
                            .commit();
                } else {
                    email.setError("\0");
                    password.setError("\0");
                    Toast.makeText(getActivity().getApplicationContext(), "Email sau parolă incorectă", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}