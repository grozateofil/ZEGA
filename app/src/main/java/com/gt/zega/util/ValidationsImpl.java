package com.gt.zega.util;

import android.content.Context;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.gt.zega.R;
import com.hbb20.CountryCodePicker;

import java.util.regex.Pattern;

public class ValidationsImpl implements Validations {

    @Override
    public boolean textInputLayoutValidation(TextInputLayout textInputLayout) {
        Context context = textInputLayout.getContext();
        String firstNameString = textInputLayout.getEditText().getText().toString();
        if (firstNameString.isEmpty() || firstNameString.matches("\\s*")) {
            textInputLayout.setError(context.getResources().getText(R.string.required));
            return false;
        } else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }


    @Override
    public boolean phoneNumberValidation(TextInputLayout phoneNumber, CountryCodePicker ccp) {
        Context context = phoneNumber.getContext();
        String phoneNumberString = phoneNumber.getEditText().getText().toString();
        if (phoneNumberString.isEmpty()) {
            phoneNumber.setError(context.getResources().getText(R.string.required));
            return false;
        } else if (!ccp.isValidFullNumber()) {
            phoneNumber.setError(context.getResources().getText(R.string.incorrectPhoneNumber));
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public boolean emailValidation(TextInputLayout email) {
        Context context = email.getContext();
        String emailString = email.getEditText().getText().toString();
        if (emailString.isEmpty()) {
            email.setError(context.getResources().getString(R.string.required));
            return false;
        } else if (!Pattern.compile("^[\\w.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(emailString).matches()) {
            email.setError(context.getResources().getString(R.string.invalidEmail));
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public boolean passwordValidation(TextInputLayout password) {
        Context context = password.getContext();
        String passwordString = password.getEditText().getText().toString();
        if (passwordString.isEmpty()) {
            password.setError(context.getResources().getText(R.string.required));
            return false;
        } else if (Pattern.compile("^(?=.*\\s).+$").matcher(passwordString).matches()) {
            password.setError("Parola nu poate să conțină spațiu gol");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public boolean createPassword(TextInputLayout password) {
        Context context = password.getContext();
        String passwordString = password.getEditText().getText().toString();
        if (passwordString.isEmpty()) {
            password.setError(context.getResources().getText(R.string.required));
            return false;
        } else if (Pattern.compile("^(?=.*\\s).+$").matcher(passwordString).matches()) {
            password.setError("Parola nu poate să conțină spațiu gol");
            return false;
        } else if (passwordString.length() < 6) {
            password.setError("Parola trebuie să conțină minim 6 caractere");
            return false;
        }
//        else if (!Pattern.compile("^(?=.*\\p{Upper}).+$").matcher(passwordString).matches()) {
//            password.setError("Parola trebuie să conțină minim o majusculă");
//            return false;
//        }
//        else if (!Pattern.compile("^(?=.*\\p{Digit}).+$").matcher(passwordString).matches()) {
//            password.setError("Parola trebuie să conțină minim o cifră");
//            return false;
//        }
//        else if (!Pattern.compile("^(?=.*\\p{Punct}).+$").matcher(passwordString).matches()) {
//            password.setError("Parola trebuie să conțină minim un caracter special");
//            return false;
//        }
        else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public boolean textViewValidation(TextView materialTextView) {
        Context context = materialTextView.getContext();
        String firstNameString = materialTextView.getText().toString();
        if (firstNameString.isEmpty() || firstNameString.matches("\\s*")) {
            materialTextView.setError(context.getResources().getText(R.string.required));
            return false;
        } else {
            materialTextView.setError(null);
            return true;
        }
    }

}
