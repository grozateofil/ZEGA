package com.gt.zega.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gt.zega.R;
import com.gt.zega.entity.User;
import com.gt.zega.entity.UserAccount;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;
import com.hbb20.CountryCodePicker;


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

    private Validations validations;

    private Button createButton;
    private Button backToLoginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        ccp = view.findViewById(R.id.ccpRegistration);
        phoneNumber = view.findViewById(R.id.phoneNumberRegistration);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        createButton = view.findViewById(R.id.createButton);
        backToLoginButton = view.findViewById(R.id.backToLoginButton);

        ccp.registerCarrierNumberEditText(phoneNumber.getEditText());
        ccp.setCustomMasterCountries(getText(R.string.europeanCountries).toString());
        ccp.setDialogEventsListener(dialogEventsListener());

        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        validations = new ValidationsImpl();

        firstname.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());
        lastname.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());
        phoneNumber.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());
        email.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());
        password.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());

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
                getActivity().onBackPressed();
                break;
        }
    }


    private CountryCodePicker.DialogEventsListener dialogEventsListener() {
        return new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {
                TextView title = dialog.findViewById(com.hbb20.R.id.textView_title);
                title.setText(R.string.selectACountry);

                EditText search = dialog.findViewById(com.hbb20.R.id.editText_search);
                search.setHint(R.string.search);
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {
            }
        };
    }

    private boolean validation() {
        return !(!validations.firstNameValidation(firstname) |
                !validations.lastNameValidation(lastname) |
                !validations.phoneNumberValidation(phoneNumber, ccp) |
                !validations.emailValidation(email) |
                !validations.createPassword(password));
    }

    private void firebaseRegistration() {
        String firstName = firstname.getEditText().getText().toString().replaceFirst("\\s++$", "").replaceFirst("^\\s*", "");
        String lastName = lastname.getEditText().getText().toString().replaceFirst("\\s++$", "").replaceFirst("^\\s*", "");
        String phoneNumber = ccp.getFullNumberWithPlus();
        String emailAddress = email.getEditText().getText().toString();
        String pass = password.getEditText().getText().toString();

        UserAccount userAccount = new UserAccount(emailAddress, pass);
        User user = new User(firstName, lastName, phoneNumber);

        fAuth.createUserWithEmailAndPassword(userAccount.getEmail(), userAccount.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    email.setError(null);
                    email.setErrorEnabled(false);

                    FirebaseUser firebaseUser = fAuth.getCurrentUser();
                    databaseReference.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                fAuth.setLanguageCode("ro");
//                                firebaseUser.sendEmailVerification();
//                                Toast.makeText(getActivity().getApplicationContext(), "Ți-a fost trimis un email pentru a valida adresa de email", Toast.LENGTH_LONG).show();
                                Toast.makeText(getActivity().getApplicationContext(), "Cont creat cu succes", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Eroare la înregistrare", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    email.setError("Există deja un cont asociat acestei adrese de email");
                }
            }
        });

    }

    @NonNull
    private ActionMode.Callback getActionModeCallback() {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        };
    }

}

