package com.gt.zega.util;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public interface Validations {

    public boolean textInputLayoutValidation(TextInputLayout textInputLayout);

    public boolean phoneNumberValidation(TextInputLayout phoneNumber, CountryCodePicker ccp);

    public boolean emailValidation(TextInputLayout email);

    public boolean passwordValidation(TextInputLayout password);

    public boolean createPassword(TextInputLayout password);

    public boolean textViewValidation(TextView materialTextView);

    public boolean checkBoxValidation(CheckBox checkBox, Context context);

}
