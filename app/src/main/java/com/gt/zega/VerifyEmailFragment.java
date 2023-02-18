package com.gt.zega;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("FieldCanBeLocal")
public class VerifyEmailFragment extends Fragment implements View.OnClickListener {

    private EditText digit1;
    private EditText digit2;
    private EditText digit3;
    private EditText digit4;
    private EditText digit5;
    private EditText digit6;
    private TextView phoneNumber;
    private Button resendCodeButton;
    private Button verifyButton;
    private Bundle bundle;

    private String verificationId;

    private String codeEnteredByUser;

    private FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_verify_email, container, false);
        bundle = this.getArguments();
        fAuth = FirebaseAuth.getInstance();
        phoneNumber = view.findViewById(R.id.phoneNumberTextView);
        phoneNumber.setText(bundle.get("phoneNumber").toString());
        sendVerificationCode();
        digit1 = view.findViewById(R.id.digit1);
        digit2 = view.findViewById(R.id.digit2);
        digit3 = view.findViewById(R.id.digit3);
        digit4 = view.findViewById(R.id.digit4);
        digit5 = view.findViewById(R.id.digit5);
        digit6 = view.findViewById(R.id.digit6);


        resendCodeButton = view.findViewById(R.id.resendCodeButton);
        verifyButton = view.findViewById(R.id.verifyButton);


        resendCodeButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);

        nextEditText(digit1, digit2);
        nextEditText(digit2, digit3);
        nextEditText(digit3, digit4);
        nextEditText(digit4, digit5);
        nextEditText(digit5, digit6);


        previousEditText(digit1, null);
        previousEditText(digit2, digit1);
        previousEditText(digit3, digit2);
        previousEditText(digit4, digit3);
        previousEditText(digit5, digit4);
        previousEditText(digit6, digit5);

        return view;
    }

    private void nextEditText(EditText currentEditText, EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    nextEditText.requestFocus();
                }
            }
        });

    }

    private void previousEditText(EditText currentEditText, EditText previousEditText) {
        currentEditText.setOnKeyListener((View view, int i, KeyEvent keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL && currentEditText.getText().toString().isEmpty()) {
                previousEditText.setText("");
                previousEditText.requestFocus();
                return true;

            }
            return false;

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.resendCodeButton):
                break;

            case (R.id.verifyButton):
                codeEnteredByUser = digit1.getText().toString() + digit2.getText().toString() + digit3.getText().toString() + digit4.getText().toString() + digit5.getText().toString() + digit6.getText().toString();

                if (codeEnteredByUser.isEmpty() || codeEnteredByUser.length() < 6) {
                    Toast.makeText(getContext(), "Eroare!", Toast.LENGTH_SHORT).show();
                } else {
                    //                Toast.makeText(getContext(), "Succes! - " + codeEnteredByUser, Toast.LENGTH_SHORT).show();
                    verifyCode(codeEnteredByUser);
                }
                break;

        }

    }

    private void verifyCode(String code) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
        resetPasswordWithCredential(phoneAuthCredential);

    }

    private void sendVerificationCode() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fAuth)
                        .setPhoneNumber(phoneNumber.getText().toString())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this.getActivity())
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
//            bundle.putString("verificationId", verificationId);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }
    };

    private void resetPasswordWithCredential(PhoneAuthCredential phoneAuthCredential) {
        fAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ResetPasswordFragment verifyEmailFragment = new ResetPasswordFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, verifyEmailFragment).addToBackStack(TAG)
                            .commit();
                } else {
                    Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}