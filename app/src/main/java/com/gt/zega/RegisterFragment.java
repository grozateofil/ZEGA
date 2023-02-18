package com.gt.zega;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.regex.Pattern;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout firstname;
    private TextInputLayout lastname;
    private CountryCodePicker ccp;
    private TextInputLayout phoneNumber;
    private TextInputLayout email;
    private TextInputLayout password;

    private FirebaseAuth fAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private boolean emailExists;

    private Button createButton;
    private Button backToLoginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);

        ccp = (CountryCodePicker) view.findViewById(R.id.ccpRegistration);
        phoneNumber = view.findViewById(R.id.phoneNumberRegistration);
        ccp.registerCarrierNumberEditText(phoneNumber.getEditText());
        ccp.setCustomMasterCountries(getText(R.string.europeanCountries).toString());

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);

        createButton = view.findViewById(R.id.createButton);
        backToLoginButton = view.findViewById(R.id.backToLoginButton);

        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        createButton.setOnClickListener(this);
        backToLoginButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.createButton):
                if (validation()) {
                    firebaseRegistration();
                }
                break;
            case (R.id.backToLoginButton):
//                LoginFragment loginFragment = new LoginFragment();
//                FragmentManager fragmentManager = getParentFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.content_frame, loginFragment)
//                        .commit();
                getActivity().onBackPressed();
                break;
        }
    }

    private boolean validation() {
        return !(!phoneNumberValidation() | !emailValidation(email.getEditText().getText().toString()) |
                !firstNameValidation(firstname.getEditText().getText().toString()) | !lastNameValidation(lastname.getEditText().getText().toString()) |
                !passwordValidation(password.getEditText().getText().toString()));
    }

    private boolean lastNameValidation(String lastName) {
        if (lastName.isEmpty()) {
            this.lastname.setError(getText(R.string.required));
            return false;
        } else {
            this.lastname.setError(null);
            this.lastname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean firstNameValidation(String firstName) {
        if (firstName.isEmpty()) {
            this.firstname.setError(getText(R.string.required));
            return false;
        } else {
            this.firstname.setError(null);
            this.firstname.setErrorEnabled(false);
            return true;
        }
    }

    public boolean phoneNumberValidation() {
        if (phoneNumber.getEditText().getText().toString().isEmpty()) {
            this.phoneNumber.setError(getText(R.string.required));
            return false;
        } else if (!ccp.isValidFullNumber()) {
            this.phoneNumber.setError(getText(R.string.incorrectPhoneNumber));
            return false;
        } else {
            this.phoneNumber.setError(null);
            this.phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    private boolean emailValidation(String email) {
        String regexPattern = "^(.+)@(\\S+)$";
        boolean isCorrect = Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
        if (email.isEmpty()) {
            this.email.setError(getText(R.string.required));
            return false;
        } else if (!isCorrect) {
            this.email.setError(getText(R.string.invalidEmail));
            return false;
        } else {
            this.email.setError(null);
            this.email.setErrorEnabled(false);
            return true;
        }
    }

    private void checkIfEmailExists(String emailAddress) {
        fAuth.fetchSignInMethodsForEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        emailExists = task.getResult().getSignInMethods().isEmpty();
                        if (!emailExists) {
                            email.setError("Exista deja un cont asociat acestei adrese de email!");
                        } else {
                            email.setError(null);
                            email.setErrorEnabled(false);
                        }

                    }
                });
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

    private void firebaseRegistration() {
        String firstName = firstname.getEditText().getText().toString();
        String lastName = lastname.getEditText().getText().toString();
        String phoneNumber = ccp.getFullNumberWithPlus();
        String emailAddress = email.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString();

        UserAccount userAccount = new UserAccount(emailAddress, pass);
        User user = new User(firstName, lastName, phoneNumber, userAccount);

        fAuth.createUserWithEmailAndPassword(emailAddress, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = fAuth.getCurrentUser();
                    databaseReference.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(getActivity().getApplicationContext(), "Ti-a fost trimis un email pentru a valida adresa de email", Toast.LENGTH_LONG).show();
                                getActivity().onBackPressed();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Eroare la inregistrare", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    checkIfEmailExists(emailAddress);
                }
            }
        });

    }


}