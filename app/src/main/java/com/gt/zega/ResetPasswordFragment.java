package com.gt.zega;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ResetPasswordFragment extends Fragment {

    private TextInputLayout passwordTIL;
    private TextInputLayout confirmPasswordTIL;
    private TextInputEditText passwordTIET;
    private TextInputEditText confirmPasswordTIET;
    private Button resetPasswordButton;

    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        passwordTIL = view.findViewById(R.id.resetPasswordTextInputLayout);
        passwordTIET = view.findViewById(R.id.resetPasswordTextInputEditText);
        confirmPasswordTIL = view.findViewById(R.id.confirmPasswordTextInputLayout);
        confirmPasswordTIET = view.findViewById(R.id.confirmPasswordTextInputEditText);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        resetPasswordButton.setEnabled(false);

        passwordTIET.addTextChangedListener(textWatcher);
        confirmPasswordTIET.addTextChangedListener(textWatcher);

        user = FirebaseAuth.getInstance().getCurrentUser();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.updatePassword(passwordTIL.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity().getApplicationContext(), "Parola a fost modificata!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Eroare!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        return view;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String passwordText = passwordTIET.getText().toString().trim();
            String confirmPasswordText = confirmPasswordTIET.getText().toString().trim();
            resetPasswordButton.setEnabled(!passwordText.isEmpty() && !confirmPasswordText.isEmpty() && passwordText.equals(confirmPasswordText));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}