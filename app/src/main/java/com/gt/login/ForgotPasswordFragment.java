package com.gt.login;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout phoneNumber;
    private Button submitButton;
    private Button backToLoginButton;

    private FirebaseAuth fAuth;

    private CountryCodePicker ccp;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);


        submitButton = view.findViewById(R.id.submitButton);
        backToLoginButton = view.findViewById(R.id.backToLogin);

        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
        phoneNumber = view.findViewById(R.id.forgotPasswordPhone);
        ccp.registerCarrierNumberEditText(phoneNumber.getEditText());

        fAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

            }
        };

        submitButton.setOnClickListener(this);
        backToLoginButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        boolean codeSend = false;
        switch (view.getId()) {
            case R.id.submitButton:

                if (phoneNumber.getEditText().getText().toString().isEmpty()) {
                    this.phoneNumber.setError("Camp obligatoriu");
                } else if (isValidPhoneNumber()) {
                    this.phoneNumber.setError(null);
                    this.phoneNumber.setErrorEnabled(false);
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(fAuth)
                                    .setPhoneNumber(ccp.getFullNumberWithPlus())       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(this.getActivity())                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

                    Toast.makeText(getActivity().getApplicationContext(), "SMS send", Toast.LENGTH_LONG).show();
                    codeSend = true;

                } else if(!ccp.isValidFullNumber()){
                    this.phoneNumber.setError("Formatul numarului este incorect");
                }




                if (codeSend) {
                    VerifyEmailFragment verifyEmailFragment = new VerifyEmailFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, verifyEmailFragment)
                            .commit();
                }


                break;
            case R.id.backToLogin:
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, loginFragment)
                        .commit();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public boolean isValidPhoneNumber() {
        return ccp.isValidFullNumber();
    }

}