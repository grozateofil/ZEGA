package com.gt.zega;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.gt.zega.database.Checking;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout email;
    private Button submitButton;
    private Button backToLoginButton;

    private Validations validations;

    private FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        email = view.findViewById(R.id.forgotPasswordEmail);

        submitButton = view.findViewById(R.id.submitBtn);
        backToLoginButton = view.findViewById(R.id.backToLoginBtn);

        validations = new ValidationsImpl();

        fAuth = FirebaseAuth.getInstance();

        submitButton.setOnClickListener(this);
        backToLoginButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.submitBtn):
                if (validations.emailValidation(email)) {
                    if (Checking.checkIfEmailExists(fAuth, email, "Nu există niciun cont asociat acestei adrese de email")) {
                        fAuth.sendPasswordResetEmail(email.getEditText().getText().toString())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity().getApplicationContext(), "În căteva momente vei primi un email pentru resetarea parolei", Toast.LENGTH_SHORT).show();
                                        getActivity().onBackPressed();
                                    } else {
                                        Toast.makeText(getActivity().getApplicationContext(), "Eroare la trimitere", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                break;
            case (R.id.backToLoginBtn):
                getActivity().onBackPressed();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}