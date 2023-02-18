package com.gt.zega;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout phoneNumber;
    private Button submitButton;
    private Button backToLoginButton;

    private FirebaseAuth fAuth;

    private CountryCodePicker ccp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        submitButton = view.findViewById(R.id.submitButton);
        backToLoginButton = view.findViewById(R.id.backToLogin);

        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
        phoneNumber = view.findViewById(R.id.forgotPasswordPhone);
        ccp.registerCarrierNumberEditText(phoneNumber.getEditText());
        ccp.setCustomMasterCountries(getText(R.string.europeanCountries).toString());

        fAuth = FirebaseAuth.getInstance();


        submitButton.setOnClickListener(this);
        backToLoginButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        boolean codeSend = false;
        switch (view.getId()) {
            case (R.id.submitButton):
                Bundle bundle = new Bundle();
                if (phoneNumber.getEditText().getText().toString().isEmpty()) {
                    this.phoneNumber.setError(getText(R.string.required));
                } else if (isValidPhoneNumber()) {
                    this.phoneNumber.setError(null);
                    this.phoneNumber.setErrorEnabled(false);

                    bundle.putString("phoneNumber", ccp.getFullNumberWithPlus());
                    Toast.makeText(getActivity().getApplicationContext(), "SMS send", Toast.LENGTH_LONG).show();
                    codeSend = true;
//                    sendVerificationCode();

                } else if (!ccp.isValidFullNumber()) {
                    this.phoneNumber.setError(getText(R.string.incorrectPhoneNumber));
                }


                if (codeSend) {
                    System.out.println("----------------" + codeSend);
                    VerifyEmailFragment verifyEmailFragment = new VerifyEmailFragment();
                    verifyEmailFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, verifyEmailFragment).addToBackStack(TAG)
                            .commit();
                }


                break;
            case (R.id.backToLogin):
                getActivity().onBackPressed();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public boolean isValidPhoneNumber() {
        return ccp.isValidFullNumber();
    }


}