package com.gt.zega.util;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public interface Validations {

    public boolean firstNameValidation(TextInputLayout firsName);

    public boolean lastNameValidation(TextInputLayout lastName);

    public boolean phoneNumberValidation(TextInputLayout phoneNumber, CountryCodePicker ccp);

    public boolean emailValidation(TextInputLayout email);

    public boolean passwordValidation(TextInputLayout password);

    public boolean createPassword(TextInputLayout password);


}
